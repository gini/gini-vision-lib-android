package net.gini.android.vision.screen

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_extractions.*
import net.gini.android.GiniApiType
import net.gini.android.models.SpecificExtraction
import net.gini.android.vision.GiniVision
import net.gini.android.vision.accounting.network.GiniVisionAccountingNetworkService
import net.gini.android.vision.example.BaseExampleApp
import net.gini.android.vision.network.Error
import net.gini.android.vision.network.GiniVisionNetworkCallback
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction
import org.json.JSONException
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Displays the Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference.
 *
 * A menu item is added to send feedback. The amount is changed to 10.00:EUR or an amount of
 * 10.00:EUR is added, if missing.
 */
class ExtractionsActivity : AppCompatActivity() {
    private var mExtractions: MutableMap<String, GiniVisionSpecificExtraction> = HashMap()
    private var mCompoundExtractions: Map<String, GiniVisionCompoundExtraction> = HashMap()
    private val mLegacyExtractions: MutableMap<String, SpecificExtraction> = HashMap()
    private var mExtractionsAdapter: ExtractionsAdapter<Any>? = null

    companion object {
        private val LOG = LoggerFactory.getLogger(ExtractionsActivity::class.java)
        const val EXTRA_IN_EXTRACTIONS = "EXTRA_IN_EXTRACTIONS"
        const val EXTRA_IN_COMPOUND_EXTRACTIONS = "EXTRA_IN_COMPOUND_EXTRACTIONS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extractions)
        readExtras()
        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_extractions, menu)
        menu.findItem(R.id.view_picture)?.let {
            it.isEnabled = analyzedCameraPicture != null
            it.isVisible = it.isEnabled
        }
        return true
    }

    private val analyzedCameraPicture: ByteArray?
        get() {
            val app = application as BaseExampleApp
            val networkService = app.getGiniVisionNetworkService("ScreenApi", GiniApiType.ACCOUNTING)
            return if (networkService is GiniVisionAccountingNetworkService) {
                networkService.analyzedCameraPictureAsJpeg
            } else null
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.feedback -> {
                sendFeedback()
                true
            }
            R.id.view_picture -> {
                viewPicture()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun viewPicture() {
        savePictureToFile()?.let { file ->
            try {
                FileProvider.getUriForFile(
                        this,
                        "net.gini.android.vision.screen.fileprovider",
                        file)
            } catch (e: Exception) {
                LOG.error("Error sharing the pictue {} ", file.absolutePath, e)
                Toast.makeText(this,
                        "Error sharing the picture {} " + file.absolutePath,
                        Toast.LENGTH_LONG).show()
                return
            }?.let { fileUri ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(fileUri, contentResolver.getType(fileUri))
                if (packageManager.queryIntentActivities(intent, 0).isEmpty()) {
                    Toast.makeText(this, "No image viewer app found",
                            Toast.LENGTH_LONG).show()
                } else {
                    startActivity(intent)
                }
            }
        } ?: run {
            Toast.makeText(this, "Could not write picture to file",
                    Toast.LENGTH_LONG).show()
        }
    }

    private fun savePictureToFile(): File? =
            analyzedCameraPicture?.let { picture ->
                val jpegFilename = "${Date().time}.jpeg"
                createPictureDir()?.let { picDir ->
                    val jpegFile = File(picDir, jpegFilename)
                    var fileOutputStream: FileOutputStream? = null
                    return try {
                        fileOutputStream = FileOutputStream(jpegFile).apply {
                            write(picture, 0, picture.size)
                        }
                        LOG.debug("Picture written to {}", jpegFile.absolutePath)
                        jpegFile
                    } catch (e: IOException) {
                        LOG.error("Failed to save picture to {}",
                                jpegFile.absolutePath, e)
                        null
                    } finally {
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close()
                            } catch (e: IOException) {
                                LOG.error("Closing FileOutputStream failed for {}", jpegFile.absolutePath, e)
                            }
                        }
                    }
                }
            }

    private fun createPictureDir(): File? {
        val externalFilesDir = getExternalFilesDir(null)
        val pictureDir = File(externalFilesDir, "camera-pictures")
        return if (pictureDir.exists() || pictureDir.mkdir()) {
            pictureDir
        } else null
    }

    private fun readExtras() {
        intent.extras?.getParcelable<Bundle>(EXTRA_IN_EXTRACTIONS)?.run {
            keySet().forEach { name ->
                try {
                    mExtractions[name] = getParcelable(name)!!
                } catch (e: ClassCastException) {
                    mLegacyExtractions[name] = getParcelable(name)!!
                }
            }
        }
        mCompoundExtractions = intent.extras?.getParcelable<Bundle>(EXTRA_IN_COMPOUND_EXTRACTIONS)?.run {
            keySet().map { it to getParcelable<GiniVisionCompoundExtraction>(it)!! }.toMap()
        } ?: emptyMap()
    }

    private fun setUpRecyclerView() {
        recyclerview_extractions.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ExtractionsActivity)
            adapter = when {
                mExtractions.isNotEmpty() -> ExtractionsAdapterImpl(getSortedExtractions(mExtractions),
                        getSortedExtractions(mCompoundExtractions))
                mLegacyExtractions.isNotEmpty() -> LegacyExtractionsAdapter(getSortedExtractions(mLegacyExtractions))
                else -> null
            }
        }
    }

    private fun <T> getSortedExtractions(extractions: Map<String, T>): List<T> = extractions.toSortedMap().values.toList()

    private fun sendFeedback() {
        // An example for sending feedback where we change the amount or add one if it is missing
        // Feedback should be sent only for the user visible fields. Non-visible fields should be filtered out.
        // In a real application the user input should be used as the new value.

        val amount = mExtractions["amountToPay"]
        if (amount != null) { // Let's assume the amount was wrong and change it
            amount.value = "10.00:EUR"
            Toast.makeText(this, "Amount changed to 10.00:EUR", Toast.LENGTH_SHORT).show()
        } else { // Amount was missing, let's add it
            val extraction = GiniVisionSpecificExtraction(
                    "amountToPay", "10.00:EUR",
                    "amount", null, emptyList())
            mExtractions["amountToPay"] = extraction
            mExtractionsAdapter?.extractions = getSortedExtractions(mExtractions)
            Toast.makeText(this, "Added amount of 10.00:EUR", Toast.LENGTH_SHORT).show()
        }
        mExtractionsAdapter?.notifyDataSetChanged()
        showProgressIndicator()
        val giniVisionNetworkApi = GiniVision.getInstance().giniVisionNetworkApi
        if (giniVisionNetworkApi == null) {
            Toast.makeText(this, "Feedback not sent: missing GiniVisionNetworkApi implementation.",
                    Toast.LENGTH_SHORT).show()
            return
        }
        giniVisionNetworkApi.sendFeedback(mExtractions, object : GiniVisionNetworkCallback<Void, Error> {
            override fun failure(error: Error) {
                hideProgressIndicator()
                Toast.makeText(this@ExtractionsActivity,
                        "Feedback error:\n" + error.message,
                        Toast.LENGTH_LONG).show()
            }

            override fun success(result: Void) {
                hideProgressIndicator()
                Toast.makeText(this@ExtractionsActivity,
                        "Feedback successful",
                        Toast.LENGTH_LONG).show()
            }

            override fun cancelled() {
                hideProgressIndicator()
            }
        })
    }

    private fun legacySendFeedback() {
        val documentTaskManager = (application as BaseExampleApp).giniApi.documentTaskManager
        // An example for sending feedback where we change the amount or add one if it is missing
        // Feedback should be sent only for the user visible fields. Non-visible fields should be filtered out.
        // In a real application the user input should be used as the new value.

        val amount = mLegacyExtractions["amountToPay"]
        if (amount != null) { // Let's assume the amount was wrong and change it
            amount.value = "10.00:EUR"
            Toast.makeText(this, "Amount changed to 10.00:EUR", Toast.LENGTH_SHORT).show()
        } else { // Amount was missing, let's add it
            val extraction =
                    SpecificExtraction("amountToPay", "10.00:EUR",
                            "amount", null, emptyList())
            mLegacyExtractions["amountToPay"] = extraction
            mExtractionsAdapter?.extractions = getSortedExtractions(mLegacyExtractions)
            Toast.makeText(this, "Added amount of 10.00:EUR", Toast.LENGTH_SHORT).show()
        }
        mExtractionsAdapter!!.notifyDataSetChanged()
        val document = (application as BaseExampleApp).singleDocumentAnalyzer
                .giniApiDocument
        // We require the Gini API SDK's net.gini.android.models.Document for sending the feedback
        if (document != null) {
            try {
                showProgressIndicator()
                documentTaskManager.sendFeedbackForExtractions(document, mLegacyExtractions)
                        .continueWith<Any> { task ->
                            runOnUiThread {
                                if (task.isFaulted) {
                                    LOG.error("Feedback error",
                                            task.error)
                                    var message: String? = "unknown"
                                    if (task.error != null) {
                                        message = task.error.message
                                    }
                                    Toast.makeText(
                                            this@ExtractionsActivity,
                                            "Feedback error:\n$message",
                                            Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(
                                            this@ExtractionsActivity,
                                            "Feedback successful",
                                            Toast.LENGTH_LONG).show()
                                }
                                hideProgressIndicator()
                            }
                            null
                        }
            } catch (e: JSONException) {
                LOG.error("Feedback not sent", e)
                Toast.makeText(this, "Feedback not set:\n" + e.message,
                        Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Feedback not set: no Gini Api Document available",
                    Toast.LENGTH_LONG).show()
        }
    }

    private fun showProgressIndicator() {
        recyclerview_extractions.animate().alpha(0.5f)
        layout_progress.visibility = View.VISIBLE
    }

    private fun hideProgressIndicator() {
        recyclerview_extractions.animate().alpha(1.0f)
        layout_progress.visibility = View.GONE
    }

    private abstract class ExtractionsAdapter<T> :
            RecyclerView.Adapter<ExtractionsViewHolder>() {
        abstract var extractions: List<T>
        abstract var compoundExtractions: List<GiniVisionCompoundExtraction>
    }

    private class ExtractionsViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextName: TextView
        var mTextValue: TextView

        init {
            mTextName = itemView.findViewById<View>(R.id.text_name) as TextView
            mTextValue = itemView.findViewById<View>(R.id.text_value) as TextView
        }
    }

    private class ExtractionsAdapterImpl(
            override var extractions: List<GiniVisionSpecificExtraction>,
            override var compoundExtractions: List<GiniVisionCompoundExtraction>) : ExtractionsAdapter<GiniVisionSpecificExtraction>() {

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ExtractionsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ExtractionsViewHolder(
                    layoutInflater.inflate(R.layout.item_extraction, parent, false))
        }

        override fun onBindViewHolder(holder: ExtractionsViewHolder,
                                      position: Int) {
            extractions.getOrNull(position)?.run {
                holder.mTextName.text = name
                holder.mTextValue.text = value
            } ?: compoundExtractions.getOrNull(position - extractions.size)?.run {
                holder.mTextName.text = name
                holder.mTextValue.text = StringBuilder().apply {
                    specificExtractionMaps.forEach { extractionMap ->
                        extractionMap.forEach { (name, extraction) ->
                            append("${name}: ${extraction.value}\n")
                        }
                        append("\n")
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return extractions.size + compoundExtractions.size
        }

    }

    private class LegacyExtractionsAdapter(
            override var extractions: List<SpecificExtraction>,
            override var compoundExtractions: List<GiniVisionCompoundExtraction> = emptyList()) : ExtractionsAdapter<SpecificExtraction>() {

        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ExtractionsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ExtractionsViewHolder(
                    layoutInflater.inflate(R.layout.item_extraction, parent, false))
        }

        override fun onBindViewHolder(holder: ExtractionsViewHolder,
                                      position: Int) {
            holder.mTextName.text = extractions[position].name
            holder.mTextValue.text = extractions[position].value
        }

        override fun getItemCount(): Int {
            return extractions.size
        }

    }
}