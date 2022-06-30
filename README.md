<h1 align="center">
  <a href="https://github.com/prithvidiamond1/DiamondBot"><img src="https://i.imgur.com/ERxQB6z.png" height=300 alt="Diamond Bot"></a>
  <br>
  Diamond Bot <sup>v1</sup>
</h1>
<p align="center">
  <a href="#documentation">Documentation</a> | <a href="#build-instructions">Build Instructions</a> | <a href="#run-instructions">Run Instructions</a>
</p>
A custom Discord bot built using [Javacord](https://github.com/Javacord/Javacord), [Spring](https://spring.io/) and [MongoDB](https://www.mongodb.com/).

## Documentation
The documentation of this repository's code is available via the Javadocs made available at the following [GitHub page](https://prithvidiamond1.github.io/DiamondBot/).

**Note:** If you wish to make any contributions to the repository, make sure to generate the Javadocs that document the entire project, including the updates made by you and put them in the '/docs' directory.

## Build Instructions

For deployment on [Heroku](https://www.heroku.com/), certain changes needed to be made to the [Gradle](https://gradle.org/) build system. Once you have the latest version of Diamond Bot's source code from GitHub pulled to your local machine, follow the below instructions to successfully build Diamond Bot.

#### On IntelliJ IDEA:

* Save your current configuration and click on 'Edit Configurations...'.
* Click on the '+' button in the top left corner of the Configurations window to add a new configuration.
* In the dropdown list that appears, select the 'Gradle' option.
* Set the name to 'Build JAR'.
* Under the run section, type `clean bootJar` and make sure the 'Gradle Project' selected is the current project's name.
* Once that is done, you are ready to build. After making the changes you wish to the code in your local repository, switch your configuration to 'Build JAR'.
* Click the build button for the configuration. Once it has finished, click the run button for the configuration.
* An output window should pop-up showing you the tasks Gradle is performing. Once done, you should see a 'BUILD SUCCESSFUL' message indicating that the build was successful, at the bottom of the output window. 
Verify this by checking if a JAR file has generated in the '/build/libs' directory.
* If the build was unsuccessful, Gradle will provide you with some debug info so that you can find the problem yourself and fix it.

Now your code is ready to be committed and pushed to the remote repository on GitHub. Heroku will automatically pull the latest version from the repository and run that instance.

**Note:** If you make changes to the 'build.gradle' file or if it is the first time Gradle is being run for the project, you will see a Gradle sync button on the screen. 
Make sure to click it to allow Gradle to fetch the necessary libraries required for building the project.

## Run Instructions

If you wish to run the JAR file locally, you can do so by creating a 'Run JAR' configuration in your IDE.

#### On IntelliJ IDEA:

* Click on the '+' button in the top left corner of the Configurations window to add a new configuration.
* In the dropdown list that appears, select the 'JAR Application' option.
* Set the name to 'Run JAR'.
* Provide the path to the JAR, as well as any necessary **environment variables**.
* Save the configuration, select it from the configurations' dropdown menu and click the run button.
* A window should pop-up, showing the runtime messages from the JAR application's console. If all goes smoothly, you 
should see a 'Bot has started!' message in the console.

If there are any runtime errors or other problems, you should see a stack trace indicating or hinting at the source of the problem. Create an issue/pull request to bring this to the notice of other contributors so that it can be resolved together.
