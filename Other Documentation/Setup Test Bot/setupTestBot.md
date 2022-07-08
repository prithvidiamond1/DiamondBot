## Setup Test Bot
This guide is to help a person set up a test bot for their local development and testing purposes.

**_NOTE: Do not try to impersonate Diamond Bot on any Discord server except on a server meant for local development and testing purposes (a server that is not exposed to the public)._**

### Instructions
To successfully set up a test bot that can be hosted locally, there are a number of requirements that need to be fulfilled. These requirements can be grouped into two categories:
- System Requirements - These are requirements that need to be satisfied by the system for the bot to even begin running on your local machine.
- Runtime Requirements - These are requirements that need to be satisfied for the bot to run without any errors from Java and its dependencies.

The System Requirements are as follows:
- Java 17 or higher
- Gradle 7.4.2 or higher

The Runtime Requirements are as follows:
- The JAR file ([DiscordBot.jar](https://github.com/prithvidiamond1/DiamondBot/blob/master/build/libs/DiscordBot.jar))
- Gradle Dependencies (check the [build.gradle](https://github.com/prithvidiamond1/DiamondBot/blob/master/build.gradle) file for this)
- Execution Environment Variables

Everything except the execution environment variables have been provided via the repository or are available on the internet to download and install. We will mainly go over what the Execution Environment Variables are and how to get them.

#### Execution Environment Variables
There are currently 5 required Execution Environment Variables. They are as follows:
- The Discord Bot Token
- The MongoDB Atlas Database Name
- The MongoDB Atlas Username
- The MongoDB Atlas Password
- The YouTube API Key

#### Discord Bot Token
The Discord Bot Token is required to authenticate and link the code to the bot provided by [Discord's Developer Portal](https://discord.com/developers).

To get a Discord Bot Token, one needs to first create a Discord app in the Discord Developer Portal. After that, create and configure a bot, during which, you will generate a bot token. For more details about this process, refer to [Discord's guide](https://discord.com/developers/docs/getting-started#creating-an-app) on this topic.

#### MongoDB Atlas Credentials
The MongoDB Atlas Database serves as the backend for the bot's persistent data storage and retrieval needs. However, connecting to a MongoDB Atlas Database requires the user to provide the above-mentioned credentials to the [MongoDB driver API](https://www.mongodb.com/docs/drivers/).

To get the required MongoDB Atlas credentials, one needs to first create a [MongoDB Atlas account](https://www.mongodb.com/cloud/atlas/register). After creating an account, use the `Build a cluster` button to create a new cluster. Follow the onscreen steps. After that you can create a new collection by using the `+ Create` button. After following the onscreen steps for that, you need to add a database to that collection. During this process, you will be asked to create a new database user with a username and password. Those are the required MongoDB Atlas Username and Password credentials. Once you have created a new user, set a meaningful name for this database as this will be your MongoDB Atlas Database Name.

For more information about these credentials and how to get them, refer to the following links:
- [Get Started with MongoDB Atlas](https://www.mongodb.com/docs/atlas/getting-started/)
- [Configure Database Users](https://www.mongodb.com/docs/atlas/security-add-mongodb-users/)

#### YouTube API Key
The YouTube API Key is necessary to access metadata of YouTube videos and playlists. This information is used for track loading and queuing purposes by the bot's audio player and track scheduler.

To get a YouTube API Key, one must have a [Google Cloud Platform Developer account](https://console.developers.google.com/). If you don't have one, create one and follow the instructions provided on [this guide by Google](https://developers.google.com/youtube/registering_an_application) to get your YouTube API Key.

#### Mapping Credentials to Environment Variables
Once you have all the required credentials, it is time to map them as Environment Variables. Depending on the operating system being used, there are different methods to do this process. However, the important part of this task is to map all the credentials with the following variable names:
- The Discord Bot Token as `BOT_TOKEN`
- The MongoDB Atlas Database Name as `DB_DBNAME`
- The MongoDB Atlas Username as `DB_USERNAME`
- The MongoDB Atlas Password as `DB_PASSWORD`
- The YouTube API Key as `YT_API_KEY`

For a guide on setting environment variables, follow this [blog post by Twilio](https://www.twilio.com/blog/2017/01/how-to-set-environment-variables.html).

_Note: If, for any reason, you need to rename the bot's environment variables to something other than the variable names provided above, you will need to change the names in the source code and recompile them. If you are using an IDE, perform a global search for the above variable names and rename them. After that build to generate a new custom JAR file. If you need help building the JAR file, refer to the [build instructions](https://github.com/prithvidiamond1/DiamondBot#build-instructions)._

And, you are done! If you completed all of the above instructions, you should be able to run the bot's JAR file without any runtime errors from Java or their dependencies. If you need help with running the bot, refer to the [run instructions](https://github.com/prithvidiamond1/DiamondBot#run-instructions).

