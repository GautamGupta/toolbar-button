![Toolbar Button Example](https://raw.githubusercontent.com/GautamGupta/toolbar-button/master/art/toolbar-button.gif)

Android Toolbar Button Library
==============================

The problem with anchoring a floating action button to a collapsing toolbar is that the CTA gets
hidden on scroll. This library allows you to artificially add a button in the toolbar with an
animation as soon as the FAB hides itself.

Works with Android 4.0+ (`minSdkVersion 14`).


Gradle
------

```
dependencies {
    ...
    compile 'com.android.support:design:25.3.1'
    compile 'am.gaut.android.toolbarbutton:toolbarbutton:0.1.0'
}
```

Usage
-----

Add this at the same level where your floating action button is defined in the activity.
[Example](https://github.com/GautamGupta/toolbar-button/blob/master/app/src/main/res/layout/activity_detail.xml#L155-L169).

```xml
<am.gaut.android.toolbarbutton.ToolbarButton
        android:id="@+id/btn_toolbar_checkin"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        style="?attr/borderlessButtonStyle"
        android:background="@drawable/selector_toolbar_button"
        android:textAppearance="@style/TextAppearance.AppCompat.Widget.Button.Inverse"
        android:paddingLeft="@dimen/toolbar_button_padding"
        android:paddingRight="@dimen/toolbar_button_padding"
        android:drawablePadding="@dimen/toolbar_button_padding"
        android:drawableLeft="@drawable/ic_message_white_18dp"
        android:drawableStart="@drawable/ic_message_white_18dp"
        android:text="@string/checkin"
        app:layout_anchor="@id/appbar"
        app:layout_anchorGravity="right|end" />
```

You can treat the view as a
[Button](http://developer.android.com/reference/android/widget/Button.html).
Properties in the snippet produce a similar result as in the screen capture.

`@dimen/toolbar_button_padding` (16dp), `@drawable/selector_toolbar_button`
([see](https://github.com/GautamGupta/toolbar-button/blob/master/toolbarbutton/src/main/res/drawable/selector_toolbar_button.xml),
[v21](https://github.com/GautamGupta/toolbar-button/blob/master/toolbarbutton/src/main/res/drawable-v21/selector_toolbar_button.xml))
are supplied by the library.

`@drawable/ic_message_white_18dp` is a [material icon](https://design.google.com/icons/#ic_message).

`?attr/borderlessButtonStyle` and `@style/TextAppearance.AppCompat.Widget.Button.Inverse` come from
the design support library.

Credits
-------
 - [Gordon Evans](https://www.linkedin.com/in/gjrevans) for concept
 - Awesome folks at [Wirkn](http://wirkn.com/) (where the idea originated from), h/t [Joseph Voung](https://www.linkedin.com/in/josephwongcl)
 - [Cheesesquare](https://github.com/chrisbanes/cheesesquare) by Chris Banes for the material design demo app

Troubleshooting
---------------

**Q. The button hides on scroll. Why is this happening?**

The default elevation of 6dp is getting overridden, making it compete with the elevation of the
appbar. Try adding `android:elevation="6dp"` property on the view.

**Q. Does this work for Android versions before 4.0?**

It can be made to work for previous Android versions as well, using different implementation classes
like the Floating Action Button. If you take up the initiative, please submit a PR.

License
-------

```
Copyright 2016 Gautam Gupta

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
```