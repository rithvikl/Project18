# Gather

## About Us

Rithvik Lagisetti

Melinda Robertson

We are a team of two Computer Science students working on our Bachelor's. This project is for a mobile applications class and is our first attempt at a mobile app.

[Team Journal](https://docs.google.com/document/d/18NY9tPLbTxDZxDIRLnaimti-xDLj1KG39Kap7Xw8rKE/edit?usp=sharing)

[Project Proposal and Summary](https://docs.google.com/document/d/1gxBmpmG7IWXES7jd5yVbO8Ojqg-bZDBxOBDmKTWqzUs/edit?usp=sharing)

## About the App

Gather is a mobile electronic scrapbook designed to allow users to carry their most precious memories in their pocket. Gather allows users to have a perpetuating record of events by taking pictures, adding comments and sharing them with loved ones.

###Notes

* Tags are used to categorize events. Tags are listed in a comma delimited list for each event. Example: family,summer,picnic

###Features

* Account: An account ensures you will not lose your events by backing up your data on a server.
* Take pictures: Take pictures with your phone and immediately back them up on a persistent storage cloud server.
* Save events: Say something about your picture, give it a title and categories.
* View events: See events that you have previously saved to remember the good times.
* Filter events: Search your events by a particular tag, the category you gave the event when it was created.
* Share: Sharing is caring! Let your friends know how awesome you are by sharing pictures straight from Gather.
* Edit events: Edit events that you have created by changing the title, date, comment and tags.

###How to Use

1. Registering
  * Open the app
  * Click on Register
  * Enter your email, birthdate and password
  * Click on Submit to register
2. Logging in
  * Open the app
  * Enter your email and password
  * Click on Sign in
3. View events
  * Login and you will see the list of events you have created
  * Tap on one of the events to view the picture and its details
4. Filter events
  * Tap on the magnifying glass icon on the top menu bar
  * Type the name of a tag that you used when you created an event
  * Tap the magnifying glass icon on the phone's keyboard to filter
5. Create events
  * Login
  * Tap the camera icon on the top menu bar
  * Take a picture!
  * Enter the title, date, comment and tags you want.
  * Touch save to save your event.
6. Share events
  * Login
  * Tap on an event in the list
  * Tap on the fab (little button on the bottom right)
  * Choose the application you want to share with (like email)
  * Follow directions for that app to send the photo
7. Edit events
  * Login
  * Tap on an event in the list
  * Tap on the Edit button at the bottom
  * Change the information (title, date, comment and tags)
  * Tap on Save
8. Logout
  * Once you've logged in you can logout by tapping on 'Logout' at the top right in the top menu.

## Development Notes

The app is working! New users can register an account and upload photos however there may still be bugs.

This section described detaily things about the app that only a few people would be interested in. If you really want to know our setup and reasoning then read on.

###Additions and Substractions

1. Registering in the first iteration was difficult because we used text fields to enter the user's birthdate. We changed it to use a date picker dialog.
2. Due to time contraints we could not implement:
  * support for users forgetting their password
  * storing the time when the picture was taken
  * all the extra features we mentioned we would have worked on if there was time
3. By accident, the password requirement was set to six characters instead of the eight we mentioned in our plan. The password requirement has now been set to eight.
4. 

###Data Storage

The data for our app is stored in three places:

* Persistant storage of user information via MySQL database.
* Temporary storage of event and user data on the user's phone via SQLite and SharedPreferences.
* Persistant storage of user photographs using the third party Cloudinary.

We used the MySQL database because we wanted to have full control over the user's account information. We considered storing user photographs in this database or on the same server but we ran into a few problems. First, storing large binary files in a database is very inefficient. To offset this we tried to store the file location only and store them in a folder on the same server. Unfortunately we could not get this to work because we don't have full access to the permissions on the server. Rithvik managed to find a third party cloud server than could store our users' photographs and this is what we are using now. Our setup now has the user data on the MySQL database and the photographs on Cloudinary.

SharedPreferences was the logical choice when deciding how to determine if the user is logged in. By storing the user data in SharedPreferences we could easily see anywhere in the app if the user is logged in and access information about the user to converse with the server.

SQLite made it easy to sort and filter search results for the event list. The title, comment, date and tags are stored in this temporary database so that the app does not have to transfer data all the time from the server.
