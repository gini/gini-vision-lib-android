# Package net.gini.android.vision.digitalinvoice

Contains the Activity and Fragments used for the return assistant's Digital Invoice Screen.

## Screen API

The [net.gini.android.vision.digitalinvoice.DigitalInvoiceActivity] is launched by the [net.gini.android.vision.analysis.AnalysisActivity] when line
item extractions are available. It displays the line items extracted from an invoice document and their total price. The user can deselect line
items which should not be paid for and also edit the quantity, price or description of each line item. The total price is always updated to
include only the selected line items.

## Component API

To use the Component API you have to include the [net.gini.android.vision.digitalinvoice.DigitalInvoiceFragment] in an Activity in your app
(a dedicated Activity is recommended). To receive events from the Fragment your Activity must implement the
[net.gini.android.vision.digitalinvoice.DigitalInvoiceFragmentListener] interface.

