package net.gini.android.vision.internal.qrcode;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.odml.image.ByteBufferMlImageBuilder;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 * <p>
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * QRCode detector task using Google ML Kit Barcode Scanning.
 *
 * @suppress
 */
public class QRCodeDetectorTaskMLKit implements QRCodeDetectorTask {

    private static final boolean DEBUG = true;
    private static final Logger LOG = LoggerFactory.getLogger(QRCodeDetectorTaskMLKit.class);
    private final BarcodeScanner mBarcodeScanner;

    public QRCodeDetectorTaskMLKit() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        mBarcodeScanner = BarcodeScanning.getClient(options);
    }

    @NonNull
    @Override
    public List<String> detect(@NonNull final byte[] image, @NonNull final Size imageSize,
                               final int rotation) {
        final MlImage mlImage =
                new ByteBufferMlImageBuilder(ByteBuffer.wrap(image),
                        imageSize.width, imageSize.height, MlImage.IMAGE_FORMAT_NV21)
                .setRotation(rotation)
                .build();

        final Task<List<Barcode>> processingTask = mBarcodeScanner.process(mlImage);

        try {
            if (DEBUG) {
                LOG.debug("Processing started");
            }
            final List<Barcode> barcodes = Tasks.await(processingTask);
            if (DEBUG) {
                LOG.debug("Processing finished");
            }
            if (barcodes.size() > 0 && DEBUG) {
                LOG.debug("Detected QRCodes:\n{}", barcodesToString(barcodes));
            }
            return barcodesToStrings(barcodes);
        } catch (ExecutionException | InterruptedException e) {
            LOG.error("QRCode detection failed", e);
        } catch (CancellationException e) {
            LOG.error("QRCode detection cancelled");
        } catch (Exception e) {
            LOG.error("QRCode detection failed with unknown exception" ,e);
        }
        return Collections.emptyList();
    }

    @Override
    public void checkAvailability(@NonNull final Callback callback) {
        callback.onResult(true);
    }

    @Override
    public void release() {
        mBarcodeScanner.close();
    }

    private List<String> barcodesToStrings(final List<Barcode> barcodes) {
        final List<String> qrCodes = new ArrayList<>(barcodes.size());
        for (Barcode barcode : barcodes) {
            qrCodes.add(barcode.getDisplayValue());
        }
        return qrCodes;
    }

    private String barcodesToString(final List<Barcode> barcodes) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < barcodes.size(); i++) {
            if (i != 0) {
                builder.append('\n');
            }
            final Barcode barcode = barcodes.get(i);
            builder.append(barcode.getRawValue());
        }
        return builder.toString();
    }

}
