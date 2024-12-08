# **Story Reading App**

## **Project Description**  
The Story Reading App is a mobile application designed to provide a seamless reading experience for users. It allows readers to browse, read, and bookmark stories from a vast collection of genres. With features like offline reading, personalized recommendations, and chapter tracking, the app offers an enjoyable and user-friendly platform for story lovers.

---

## **Requirements**  

### **Development Tools**  
- **Android Studio** (Latest version)  
- **Java Development Kit (JDK)** version 8 or higher  

### **Libraries and SDKs**  
The app uses the following dependencies in its `build.gradle` file:  
- **Firebase Core**: For user authentication, real-time database, and storage.  
- **Glide**: For loading and displaying story cover images.  
- **RecyclerView**: For dynamic story and chapter lists.  
- **Retrofit/Volley**: For making API calls to fetch story data from external sources.  

---

 ## **Installation**

### **1. Download the App**
1. Visit the [releases page](https://github.com/vunameaut/doctruyen/releases) of this repository.
2. Download the latest APK file (e.g., `StoryReadingApp-v1.0.apk`).

### **2. Install the App**
1. Transfer the downloaded APK file to your Android device if you downloaded it on a PC.
2. On your Android device:
   - Open **Settings** > **Security** > Enable **Unknown Sources** to allow installations from outside the Play Store.
3. Locate the APK file using a File Manager and tap on it to install.

### **3. Open the App**
Once installed, you can open the app from your app drawer and start using it.

---

### **Optional for Developers**
If you want to build the app manually, follow these steps:

#### **Clone the Project**
```bash
git clone https://github.com/vunameaut/doctruyen.git  
 
 
### **Clone the Project**  
Clone this repository to your local environment:  
```bash
git clone https://github.com/vunameaut/doctruyen.git  
Setup Firebase
Go to Firebase Console.
Create a new project and download the google-services.json file.
Add the google-services.json file to the app directory of your project.
Build the Project
Open the project in Android Studio and sync Gradle files to install dependencies.

Usage
User Features
Browse Stories: Explore a wide range of genres and categories.
Read Stories: Access story chapters with a clean and intuitive reading interface.
Bookmark: Save your favorite stories and continue reading from where you left off.
Offline Reading: Download chapters to read without an internet connection.
Recommendations: Get personalized story suggestions based on your reading preferences.
Admin Features
Story Management:
Add, edit, or delete stories and chapters.
Analytics: Monitor popular stories and user engagement.
Run the App
Install the APK on your Android device and log in to explore the reading and administrative features.

How It Works
Story Database: Firebase Realtime Database stores story metadata, chapters, and user progress in real-time.
User Authentication: Firebase Authentication handles secure user login and account management.
Story Loading: Story covers and content are loaded dynamically using Glide and API calls.
Offline Support: Chapters are cached locally for offline access.
Future Improvements
Implement dark mode for better reading experiences at night.
Add audio storytelling features.
Optimize recommendation algorithms with AI.
Author
Author: Vũ Hoài Nam
Email: 20212501@eaut.edu.vn
GitHub: https://github.com/vunameaut Thousand nine thirteen.