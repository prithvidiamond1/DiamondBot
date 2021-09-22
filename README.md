# Diamond Bot
A custom Discord bot built using [Javacord](https://github.com/Javacord/Javacord) and [Spring](https://spring.io/).

### Build Instructions
For deployment on [Heroku](https://www.heroku.com/), certain changes needed to be made to the [Gradle](https://gradle.org/) build system. Once you have the latest version of Diamond Bot's source code from Github pulled to your local machine, follow the below instructions to successfully build Diamond Bot.

On IntelliJ IDEA:
* Save your current configuration and click on 'Edit Configurations...'.
* Click on the '+' button in the top left corner of the Configurations window to add a new configuration.
* In the dropdown list that appears, select the 'Gradle' option.
* Set the name to 'Build JAR'.
* Under the run section type `stage` and make sure the 'Gradle Project' selected is the name of the current project.
* Once that is done, you are ready to build. After you have made the changes you wish to the code in your local repository, build your java files separately and switch your configuration to 'Build JAR'.
* Open the 'build.gradle' file and check for any Gradle changes to be applied. If there are any to be applied, apply them and if not, leave it as is.
* Now click the build button and wait for it build the changes for Gradle.
* Once that is done, click the run button and an output window should pop-up showing you the tasks Gradle is performing and once it is done, at the bottom of the output window, you should see a 'BUILD SUCCESSFUL' message indicating to you that the build was successful.

Now your code is ready to be committed and pushed to the remote repository on Github. Heroku will automatically pull the latest version from the repository and run that instance.
