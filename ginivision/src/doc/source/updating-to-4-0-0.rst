Updating to 4.0.0
====

Migrating to this version should be straightforward. The only change which might have a big impact is that we now use the AndroidX
libraries instead of the discontinued Android Support libraries.

AndroidX
----

We postponed migrating to AndroidX as long as we could, but we encountered a critical issue when a client uses AndroidX with GVL 4.0.0. We
expect most apps by now have migrated or will migrate in the near future to AndroidX.

In case you haven't migrated to AndroidX and would like to update to GVL 4.0.0 you can find extensive documentation about migrating to
AndroidX in the official Android documentation.