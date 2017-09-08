AndroidiCloud is a Android port of the [PyiCloud](https://github.com/picklepete/pyicloud) library. It provides a means for Android applications to use iCloud web services.
It is licenced under the MIT licence.

# Authentication

Authentication is as simple as passing your username and password to the ``AndroidiCloudService`` class:

    AndroidiCloudService icloud = new AndroidiCloudService("{example@icloud.com}", "{Your Password}");

Remember to call in background mode (thread/async task).

# Contacts

You can access your iCloud contacts/address book through the ``contacts`` method:

    JSONArray contacts = icloud.contacts().all();
    
Remember to call in background mode (thread/async task).