# Android Swipe Actions
This library helps to add swipe actions to a RecyclerView. A swipe action is an action which is performed when a swipe gesture is recognized on an adapter item. The library contains an delete action with an undo option which makes it usable out of the box. </br></br>
![Imgur](https://i.imgur.com/qpULJkM.gif)</br></br>
Note: Keep in mind that any action can be implemented! 

# Setup
Add jitpack.io to your repositories build.gradle file:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

And then add the dependency
```
dependencies {
    ...
    implementation 'com.github.TreesAreOP:android-swipe-actions:CURRENT_VERSION'
    
    // use compile for older Android Studio versions
    // compile 'com.github.TreesAreOP:android-swipe-actions:CURRENT_VERSION'
}
```

# Usage
To get started with the library you should first implement your own action or use the implemented delete action.
This chapter will show the usage of the delete action and the usual workflow to assign the actions to the RecyclerView.
Custom Actions will be described in the respective chapter ([Custom Actions](#customAction)).

## The Delete Action
The delete action can be instantiated like this:
```kotlin
val pendingDeleteAction = DeleteAction(yourRecyclerView, R.id.container)
val instantDeleteAction = DeleteAction(yourRecyclerView, R.id.container, // necessary
                false, // disable undo
                iconID = R.drawable.ic_delete_forever_black_24dp, // icon drawable, cou can also specify the icons color!
                colorID = R.color.colorInstantDelete, // background color
                name = "instantDelete") // name of the action --> use this for debugging! otherwise not used
```
The pendingDeleteAction uses the undo feature. Every other parameter besides the RecyclerView and SnackBar parent are optional.
To achieve an instant delete you could just set the undo parameter to false. If you'd like to customize the color and icon of the action you can also do so.

Furthermore you can customize the text of the shown SnackBar and specify a callback when items are deleted. 
```kotlin
// set delete snackbar Text
pendingDeleteAction.setSnackBarText(R.string.deleteUndo, R.plurals.deletedItems) 

// on delete listener
pendingDeleteAction.itemsDeletedCallback = {
}
```
The setSnackBarText method expects a string resource for the undo button text and a 
plurals string resource of the text. The plurals string should look something like this:
```xml
    <plurals name="deletedItems">
        <item quantity="one">deleted %1$d item</item>
        <item quantity="other">deleted %1$d items</item>
    </plurals>
```
This allows you to provide translations for the SnackBar. If you don't specify custom Strings a default text will be shown.
The itemsDeletedCallback expects a function with the signature (IntArray) -> Unit where the IntArray contains all deleted indices.
## Setting up the RecyclerView
Setting up the RecyclerView is relatively simple: 
```kotlin
// set alternating background colors (optional)
list.addItemDecoration(AlternatingBackgroundItemDecoration(this, R.color.colorWhite, R.color.colorGrey))

// set up RecyclerView
SwipeActionSetupHelper.setUpRecyclerView(this@MainActivity, list, pendingDeleteAction, pendingDeleteAction)
```
You can use alternating Background colors if you want to. To set up the RecyclerView use the SwipeActionSetupHelper
as shown above. 

Your used adapter needs to implement the SwipeActionAdapter interface and provide the given methods. 
An example implementation can be found in the sample app. It's important to note that the add and remove methods need to implement 
the corresponding notify methods of the adapter.

## <a name=customAction></a>Custom Actions
Android Swipe Actions allows you to implement your own SwipeActions. Your not limited in what you can do! To get started you need to 
extend the SwipeAction abstract class. See the sample for an example implementation. The performAction method will be called when
a swipe is registered. 
```kotlin
abstract fun performAction(swipedHolder: RecyclerView.ViewHolder, swipeDirection: Int)
```
Information of the swiped item can be retrieved from the ViewHolder. 
Originally the adapter position was used for the method call. This was not accurate because items could change their position in the adapter. To prevent index errors
the ViewHolder of the given item is used. This provides the benefit of accurate indices since the adapterPosition of the ViewHolder is updated. 
However there is also the possibility of the ViewHolder being recycled or other magic happening internally. To be save you could check if the adapterPosition makes sense (e.g. not -1 and not higher than the adapter size).
</br></br>
And thats all there is to it! Have fun adding those actions to your RecyclerViews!

# License

Copyright 2017 Adrian Bernhart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
