Updating to 4.0.0
====

Migrating to this version should be straight forward. The only change which might have a big impact on you is that we now use the AndroidX
libraries instead of the discontinued Android Support libraries.

Return Assistant
----

The main feature of this releas is the Return Assistant. Users often order multiple items and decide to return some of them. The Return
Assistant helps them by showing the line items from the scanned invoice. They can then deselect the items they are returning and the total
price is automatically updated to contain only the items they are keeping. This dynamically calculated total price is returned to the client
applications in the ``amountToPay`` extraction.

Users can also edit the line items shown in the Return Assistant. We extended GVL's API to also return the line items including any
modifications the users have made.

Requirements
^^^^

To use this feature your client id must be configured to include line item extractions.

Enable the Return Assistant
^^^^

The Return Assistant is disabled by default. Enable it when building a new ``GiniVision`` instance:

.. code-block:: java

     GiniVision.newInstance()
                .setReturnAssistantEnabled(true)
                .build();

Screens
^^^^

The Return Assistant consists of two screens: the Digital Invoice Screen and the Line Item Details Screen.

Digital Invoice Screen
~~~~

This is the main screen of the Return Assistant. It displays the line items from the invoice along with their total price.

If you use the Screen API, then this screen is shown by the Analysis Screen when line item extractions were received.

If you use the Component API, then you need to implement the new ``AnalysisFragmentListener.onProceedToReturnAssistant()`` method and
show the ``DigitalInvoiceFragment`` when its called.

Users can deselect line items and the total price is updated accordingly. By tapping on a line item users can go to the Line Item Details
Screen to edit it.

The updated total price and the line items are returned to your application when the user taps the pay button.

Customizing the UI
++++

Detailed description of the customization options is available in the
`Customization Guide <customization-guide.html#digital-invoice-screen>`_.

Line Item Details Screen
~~~~

This screen shows the details of a line item and allows the user to edit them. The changes are taken over by the Digital Invoice Screen when
the user taps on the save button.

If you use the Screen API, then this screen is shown by the Digital Invoice Screen when a user taps a line item.

If you use the Component API, then you need to implement the ``DigitalInvoiceFragmentListener`` and show the ``LineItemDetailsFragment``
when the ``onEditLineItem()`` method is called.

Customizing the UI
++++

Detailed description of the customization options is available in the
`Customization Guide <customization-guide.html#line-item-details-screen>`_.

Receiving the results
^^^^

Total price of the selected line items
~~~~

The total price is returned in the ``amountToPay`` extraction. If you use the Screen API, then you don't need to change anything.

If you use the Component API, then you need to use the new signature of the ``AnalysisFragmentListener.onExtractionsAvailable()``. No other
changes needed.

Line items
~~~~

If you use the Screen API, then the ``CameraActivity`` returns an additional extra in the
``CameraActivity.EXTRA_OUT_COMPOUND_EXTRACTIONS`` containing a map of compound extraction labels as keys and the compound extractions as
values. Currently the only compound extraction returned are the line items which have the ``lineItems`` label.

If you use the Component API, then you need to use the new signature of the ``AnalysisFragmentListener.onExtractionsAvailable()`` which now
also returns the map of compound extractions. This map is identical to the one the CameraActivity returns in the Screen API.

Breaking changes
----

If you use the Component API, then you need to update your classes implementing the ``AnalysisFragmentListener``. A new
``onProceedToReturnAssistant()`` method was added which is called when you should show the return assistant. The
``onExtractionsAvailable()`` method was modified to also return the new compound extractions.

If you don't use the Return Assistant you can just implement a no-op version of ``onProceedToReturnAssistant()`` and ignore the second
parameter of ``onExtractionsAvailable()``.

AndroidX
----

We postponed migrating to AndroidX as long as we could, but we encountered a critical issue when a client uses AndroidX with GVL 4.0.0. We
expect most apps by now have migrated or will migrate in the near future to AndroidX.

In case you haven't migrated to AndroidX and would like to update to GVL 4.0.0 you can find extensive documentation about migrating to
AndroidX in the official Android documentation.