# Package net.gini.android.vision.digitalinvoice.details

Contains the Activity and Fragments used for the return assistant's Line Item Details Screen.

## Screen API

The [net.gini.android.vision.digitalinvoice.details.LineItemDetailsActivity] is launched by the
[net.gini.android.vision.digitalinvoice.DigitalInvoiceActivity] when the user tapps on a line item to edit it. It displays the line item and
allows editing it.

## Component API

To use the Component API you have to include the [net.gini.android.vision.digitalinvoice.details.LineItemDetailsFragment] in an Activity in
your app (a dedicated Activity is recommended). To receive events from the Fragment your Activity must implement the
[net.gini.android.vision.digitalinvoice.details.LineItemDetailsFragmentListener] interface.