CWAC MMCursor: Ch-Ch-Ch-Ch-Ch-Ch-Ch-Changes
===========================================

`MatrixCursor`, as provided by Android, is nice, but you
cannot modify its contents. That is annoying at times.

`MutableMatrixCursor` is a slower-performing implementation
of the same API, but also adds a `remove()` method to allow
you to get rid of a row by position. Note that this resets
the cursor position to where `isBeforeFirst()` will return
`true`, so be careful when you remove rows.

Usage
-----
Full instructions for using this module are forthcoming. Stay
tuned!

Dependencies
------------
None.

Version
-------
This is version 0.0.1 of this module, meaning it is pretty darn
new. It is so new, there isn't even a public sample yet.

Demo
----
TBD

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please
join and ask them on the [cw-android Google Group][gg]. Be sure to
indicate which CWAC module you have questions about.

[gg]: http://groups.google.com/group/cw-android
