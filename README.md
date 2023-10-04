# Synopsys Security Scan Plugin

This repository contains a Jenkins plugin implemented as a Gradle project. The plugin provides functionality for performing Synopsys Security Scan with Black Duck, Coverity and Polaris. This README.md file serves as a guide for developers and users of the plugin. Please note that this Jenkins plugin currently supports only **Bitbucket as the source code management (SCM) system.**

# Quick Start for the Security Scan Plugin

## Developers Guide

To work with the project locally and run it with a Jenkins server, follow these steps:

1. Run Jenkins server locally with the plugin being deployed:
```
./gradlew server
```
> Enter https://localhost:8080 in your browser

**Note:** Make sure that **port 8080** is free on your machine, and you have 
**_Bitbucket_** and **_Pipeline_** plugin installed in you Jenkins server to configure the multibranch pipeline job.

2. Building the project will generate the plugin `hpi` file:
```
./gradlew clean build
```

The generated plugin `hpi` file can be found in the `build/libs` folder of your project directory.

### Setting up dev-test environment in docker

*  Make sure a `temp-jenkins` directory exists in your home directory.


* Run the following command to export your plugin into `hpi` format:
```
./gradlew clean build
```

* Spin up the jenkins instance with the following command:

```
docker-compose up
```
or if you prefer detach mode
```
docker-compose up -d
```

3. Install the plugin `hpi` file in your Jenkins instance:
>- Go to your Jenkins instance.
>- Navigate to **Manage Jenkins** > **Manage Plugins** > **Advanced Settings**.
>- In the **Deploy Plugin** section, click **Choose File**.
>- Select the generated `hpi` file and click **Deploy**.
>- **Restart** your Jenkins instance.

**Note:** `xcode-select` may need to be installed in **Mac** if any kind of error like - `git init` failed or developer path related error is faced while running job from jenkins instance.

Command to install `xcode-select` in Mac:
```
xcode-select --install
```

## Users Guide

### Bitbucket Prerequisites:

#### Bitbucket token for your Job Configuration:
Account-Level Bitbucket HTTP Access Token is required to configure your Job. <bR>
To Generate this token, follow these instructions:
>- Select Profile Photo → Manage Account → HTTP Access Tokens → Create token
>- Enter Token name
>- Keep everything as default or you can change the Project/Repository Permissions as your need.
>- Click the Create Button. Then a token will be generated. <br>
** You need to store this token to configure the Branch Sources of your Jenkins job

#### Bitbucket token for PrComment/FixPr:
bitbucket_token parameter is required as input when running Black Duck/Coverity PR Comment. 
There are two different types of tokens in bitbucket which can be passed to bitbucket_token
parameter.

**1. Account-Level Bitbucket HTTP Access Token:**   
To use this token for PR comments, it must hold Project permissions such as "Project write" or "Project admin." This token is employed when working both on the Project level and repository level.
We described how can we get this token on the upper section.

**2. Repository-Level Bitbucket HTTP Access Token:**   
To use this token for PR comments,
it must hold Repository permissions such as "Repository write" or "Repository admin."
This token is employed when working at the repository level. To Generate this token, follow these instructions:
>- First go to the source of your repository.
>- Click on the repostory settings icon.
>- Then click on the HTTP access tokens.
>- Next click Create token button.
>- Enter Token name.
>- Keep everything as default or you can change the Project/Repository Permissions as your need.
>- Click the Create Button. Then a token will be generated. <br>
** You need to store this token to run the Black Duck/Coverity PR Comment Feature.

### Project Setup
#### Installing Helper Plugins for Jenkins:
- **Pipeline**

To install plugins, first navigate to:  
>- Dashboard → Manage Jenkins → Plugins   
>- After that Go to the section "Available plugins".  
>- Then Search And Install the `Pipeline` plugin that we mentioned above.  
>- Once the installation is completed then restart the jenkins instance.

#### Configure Bitbucket Server:
Navigate to Dashboard → Manage Jenkins → System  
Go to the Bitbucket Endpoints section. Click to the Add button.   
Select the Bitbucket Server from the dropdown. Now follow these instructions.  
>- Enter the Name
>- Enter valid Server URL
>- Enter Server Version
>- Click checkmark to the Manage hooks. And keep everything as default.
>- Select your credentials that you configured before. In case you didn't configure credentials you can configure it from the Jenkins Credentials Provider which you can find by clicking the "Add" dropdown. Select the Kind → Username with password. Then give your bitbucket username and access token on the username and password field.
>- Select the Plugin from the "Webhook implementation to use" dropdown.
>- Click Apply and Save.

#### Create a Multibranch Pipeline Job in your Jenkins instance

To create the Multibranch Pipeline, follow these instructions,
>- First click to the New Item
>- Enter an item name
>- Select Multibranch Pipeline
>- Click OK   
Then you will be navigated to your Job's configuration page.

#### Configure The Job

First, Go to the Branch Sources section. Then follow these instructions.
>- Select your Bitbucket Server from the Bitbucket Server dropdown.
>- Select your credentials that you configured before.
>- Enter the Owner Name.
>- Enter the Repository Name. And keep everything as default.
>- Click Apply and Save.

**Note:** During the first time job configuration, jenkins triggers scan on all branches by default, if **Jenkinsfile** exists in root directory of the branch.
So to trigger only the specific branch during the first time job configuration, follow these instruction:
>- On the Property strategy dropdown, select All branches get the same properties.
>- Click Add property button below the Property strategy field.
>- Then Click on Suppress automatic SCM triggering.
>- Next on the Branch names to build automatically field → Enter your branch name. Or, if you want to include multiple branches you can use regex.
>- On the Suppression strategy dropdown, select For matching branches schedule all builds (nothing is suppressed).
>- Finally, click Apply and Save.  
**Note:** Later you may need to delete the `Suppress automatic SCM triggering` property to trigger scan on other branches by clicking `Scan Multibranch Pipeline Now` on the job.

#### Configure Global UI :
Navigate to Dashboard → Manage Jenkins → System  
Then go to the Synopsys Security Scan section.  
And from there you can populate the inputs for configuration.

#### Generate Pipeline Syntax:
>- Go to the Dashboard → JOB NAME → Branches / Pull Requests
>- Then click on the BRANCH NAME or PULL REQUEST
>- Next click on the Pipeline Syntax from the Sidebar.
>- Go to the Steps Section.
>- Select synopsys_scan: Synopsys Security Scan from the Sample Step dropdown.
>- Populate the property field.
>- Then click on the Generate Pipeline Script.
>- Finally, copy the Generated Pipeline Script to Jenkinsfile.

### Using Synopsys Security Scan for Black Duck

 To use the plugin and invoke it as a pipeline step, follow these instructions:

1. Add the following code snippet to your `Jenkinsfile` in your project root directory that you want to scan:

```groovy
stage("Security Scan") {
    steps {
        script {
            def blackDuckScanFull
            def blackDuckAutomationPrComment

            if (env.CHANGE_ID == null) {
                blackDuckAutomationPrComment = false
            } else {
                blackDuckAutomationPrComment = true
            }

            synopsys_scan product: "blackduck", blackduck_url: "BLACKDUCK_URL", blackduck_token: "YOUR_BLACKDUCK_TOKEN", 
                    blackduck_scan_full: true, blackduck_automation_prcomment: "${blackDuckAutomationPrComment}"
        }
    }
}
```
Make sure to provide the required parameters such as `blackduck_url` and `blackduck_token` with the appropriate values.

Or if the values are configured in **Jenkins Global Configuration**, you can use the following example -
```groovy
synopsys_scan product: "blackduck", blackduck_scan_full: "${blackDuckScanFull}", blackduck_automation_prcomment: "${blackDuckAutomationPrComment}"
```
**Note:** If user doesn't pass `blackduck_scan_full`  then there are three scenarios:
- For main branch, the `detect.blackduck.scan.mode = INTELLIGENT`
- For push events(any dev branch), the `detect.blackduck.scan.mode = INTELLIGENT`
- For pull requests, the `detect.blackduck.scan.mode = RAPID`

Or a very basic template - 
```groovy
synopsys_scan product: "blackduck"
```
If these values are configured in Jenkins Global Configuration, then it is not necessary to pass these values as pipeline input parameter.
Or, if these values are set both from Jenkins Global Configuration and pipeline input parameter, then pipeline input values will get preference.

2. Create a Multibranch Pipeline Job in your Jenkins instance
3. Add Bitbucket as the branch source in the job configuration
4. Scan Multibranch Pipeline

**Note:** Make sure you have **_Pipeline_** plugin installed in you Jenkins instance to configure the multibranch pipeline job.


###  List of mandatory and optional parameters for Black Duck

| Input Parameter                     | Description                                                                                                                                                                                                                       | Mandatory / Optional                                        |
|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
| `blackduck_url`                     | URL for Black Duck server. The URL can also be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `blackduck_url: "${env.BLACKDUCK_URL}"` </br>                           | Mandatory if not configured in Jenkins Global Configuration |
| `blackduck_token`                   | API token for Black Duck. The token can also be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `blackduck_token: "${env.BLACKDUCK_TOKEN}"` </br>                      | Mandatory if not configured in Jenkins Global Configuration |
| `blackduck_install_directory`       | Directory path to install Black Duck                                                                                                                                                                                              | Optional                                                    |
| `blackduck_scan_full`               | Specifies whether full scan is required or not. By default, pushes will initiate a full "intelligent" scan and pull requests will initiate a rapid scan. <br> Supported values: `true` or `false` </br>                           | Optional (Default: **false**)                               |
| `blackduck_scan_failure_severities` | Scan failure severities of Black Duck. <br> Supported values: `ALL`, `NONE`, `BLOCKER`, `CRITICAL`, `MAJOR`, `MINOR`, `OK`, `TRIVIAL`, `UNSPECIFIED`. <br> Example: `blackduck_scan_failure_severities: "BLOCKER, TRIVIAL"` </br> | Optional                                                    |
| `blackduck_automation_prcomment`    | Flag to enable automatic pull request comment based on Black Duck scan result. <br> Supported values: `true` or `false`. <br> Example: `blackduck_automation_prcomment: true` </br>                                               | Optional (Default: **false**)                               |
| `blackduck_download_url`            | When Black Duck Download URL is provided by user, Synopsys Bridge will download detect from the provided URL. <br>                                                                                                                | Optional                                                    |


### Using Synopsys Security Scan for Coverity

To use the plugin and invoke it as a pipeline step, follow these instructions:

1. Add the following code snippet to your `Jenkinsfile` in your project root directory that you want to scan:

```groovy
stage("Security Scan") {
    steps {
        script {
            def coverityAutomationPrComment

            if (env.CHANGE_ID == null) {
               coverityAutomationPrComment = false
            } else {
               coverityAutomationPrComment = true
            }

            synopsys_scan product: "coverity", coverity_url: "COVERITY_URL", coverity_user: "COVERITY_USER_NAME",
                    coverity_passphrase: "COVERITY_PASSWORD", coverity_automation_prcomment: "${coverityAutomationPrComment}"
        }
    }
}
```
Make sure to provide the required parameters such as `coverity_url`, `coverity_user` and `coverity_passphrase` with the appropriate values.

Or if the values are configured in **Jenkins Global Configuration**, you can use the following example -
```groovy
synopsys_scan product: "coverity", coverity_automation_prcomment: "${coverityAutomationPrComment}"
```
Or a very basic template -
```groovy
synopsys_scan product: "coverity"
```

If these values are configured in Jenkins Global Configuration, then it is not necessary to pass these values as pipeline input parameter.
Or, if these values are set both from Jenkins Global Configuration and pipeline input parameter, then pipeline input values will get preference.

2. Create a Multibranch Pipeline Job in your Jenkins instance
3. Add Bitbucket as the branch source in the job configuration
4. Scan Multibranch Pipeline

**Note:** Make sure you have **_Pipeline_** plugin installed in you Jenkins instance to configure the multibranch pipeline job.


###  List of mandatory and optional parameters for Coverity

| Input Parameter                 | Description                                                                                                                                                                                                                                                                                     | Mandatory / Optional                                        |
|---------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
| `coverity_url`                  | URL for Coverity server                                                                                                                                                                                                                                                                         | Mandatory if not configured in Jenkins Global Configuration |
| `coverity_user`                 | Username for Coverity                                                                                                                                                                                                                                                                           | Mandatory if not configured in Jenkins Global Configuration |
| `coverity_passphrase`           | Password for Coverity                                                                                                                                                                                                                                                                           | Mandatory if not configured in Jenkins Global Configuration |
| `coverity_project_name`         | Project name in Coverity. <br> Many customers prefer to set their Coverity project and stream names to match the SCM repository name  </br>                                                                                                                                                     | Optional                                                    |
| `coverity_stream_name`          | Stream name in Coverity                                                                                                                                                                                                                                                                         | Optional                                                    |
| `coverity_install_directory`    | Directory path to install Coverity                                                                                                                                                                                                                                                              | Optional                                                    |
| `coverity_policy_view`          | ID number/Name of a saved view to apply as a "break the build" policy. If any defects are found within this view when applied to the project, the build will be failed with an exit code. <br> Example: `coverity_policy_view: '100001'` or `coverity_policy_view: 'Outstanding Issues'`  </br> | Optional                                                    |
| `coverity_automation_prcomment` | To enable feedback from Coverity security testing as pull request comment. Merge Request must be created first from feature branch to main branch to run Coverity PR Comment. <br> Supported values: `true` or `false` </br>                                                                    | Optional (Default: **false**)                               |
| `coverity_version`              | To download the specified Coverity version rather than downloading the default latest version                                                                                                                                                                                                   | Optional                                                    |
| `coverity_local`                | To support local analysis. <br> Supported values: `true` or `false` </br>                                                                                                                                                                                                                       | Optional                                                    |


### Using Synopsys Security Scan for Polaris

To use the plugin and invoke it as a pipeline step, follow these instructions:

1. Add the following code snippet to your `Jenkinsfile` in your project root directory that you want to scan:

```groovy
stage("Security Scan") {
    steps {
        script {
            synopsys_scan product: "polaris", polaris_server_url: "POLARIS_SERVERURL", polaris_access_token: "POLARIS_TOKEN",
                    polaris_application_name: "YOUR_POLARIS_APPLICATION_NAME", polaris_project_name: "YOUR_POLARIS_PROJECT_NAME", polaris_assessment_types: "SCA, SAST"
        }
    }
}
```
Make sure to provide the required parameters such as `polaris_server_url`, `polaris_access_token`, `polaris_application_name`, `polaris_project_name` and `polaris_assessment_types` with the appropriate values.

Or if the values are configured in **Jenkins Global Configuration**, you can use the following example -
```groovy
synopsys_scan product: "polaris", polaris_application_name: "YOUR_POLARIS_APPLICATION_NAME", polaris_project_name: "YOUR_POLARIS_PROJECT_NAME", polaris_assessment_types: "SCA, SAST"
```

If these values are configured in Jenkins Global Configuration, then it is not necessary to pass these values as pipeline input parameter.
Or, if these values are set both from Jenkins Global Configuration and pipeline input parameter, then pipeline input values will get preference.

2. Create a Multibranch Pipeline Job in your Jenkins instance
3. Add Bitbucket as the branch source in the job configuration
4. Scan Multibranch Pipeline

**Note:** Make sure you have **_Pipeline_** plugin installed in you Jenkins instance to configure the multibranch pipeline job.


###  List of mandatory and optional parameters for Polaris


| Input Parameter            | Description                                                                                                                                                                                                                       | Mandatory / Optional                                        |
|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|
| `polaris_server_url`       | URL for Polaris server. The URL can also be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `polaris_server_url: "${env.BRIDGE_POLARIS_SERVERURL}"` </br>              | Mandatory if not configured in Jenkins Global Configuration |
| `polaris_access_token`     | Access token for Polaris server. The URL can also be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. <br> Example: `polaris_access_token: "${env.BRIDGE_POLARIS_ACCESSTOKEN}"` </br> | Mandatory if not configured in Jenkins Global Configuration |
| `polaris_application_name` | The application name created in the Polaris server.                                                                                                                                                                               | Mandatory                                                   |
| `polaris_project_name`     | The project name you have created in Polaris.                                                                                                                                                                                     | Mandatory                                                   |
| `polaris_assessment_types` | Specifies the type of scan you want to run. <br> Supported values: `SCA` or `SAST` or both SCA and SAST. <br> Example:  `polaris_assessment_types: "SCA, SAST"` </br>                                                             | Mandatory                                                   |
| `polaris_triage`           | Accepts only one value. <br> Supported values: `REQUIRED` or `NOT_REQUIRED` or `NOT_ENTITLED`.</br>                                                                                                                               | Optional                                                    |
| `polaris_branch_name`      | Branch name in the Polaris Server                                                                                                                                                                                                 | Optional                                                    |


### Synopsys Security Product

| Input Parameter             | Description                                                                                                                                                                         | Mandatory / Optional |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------|
| `product` | Provide the security product that you want to execute. <br> Supported values: **polaris**, **blackduck**, **coverity** <br> Example: `product: "blackduck"` </br> | Mandatory            |


### Bitbucket Parameters

| Input Parameter   | Description                                                                                                                                                                                                                       | Mandatory / Optional |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------|
| `bitbucket_token` | The token can be configured in Jenkins **Global Configuration** or can be passed as **Environment Variable**. This is required if fixpr or prcomment is set true. <br> Example: `bitbucket_token: "${env.BITBUCKET_TOKEN}"` </br> | Optional             |

### Synopsys Bridge Parameters
| Input Parameter                     | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `synopsys_bridge_install_directory` | Provide a path, where you want to configure or already configured Synopsys Bridge. <br> [Note - If you don't provide any path, then by default configuration path will be considered as - $HOME/synopsys-bridge]. If the configured Synopsys Bridge is not the latest one, latest Synopsys Bridge version will be downloaded                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| `synopsys_bridge_download_url`      | Provide URL to bridge zip file. If provided, Synopsys Bridge will be automatically downloaded and configured in the provided bridge- or default- path. <br> [Note - As per current behavior, when this value is provided, the bridge_path or default path will be cleaned first then download and configured all the time]                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `synopsys_bridge_download_version`  | Provide bridge version. If provided, the specified version of Synopsys Bridge will be downloaded and configured.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `include_diagnostics`               | If this is set **true** then the detailed bridge logs will be shown in console and bridge diagnostics will be uploaded in Jenkins Archive Artifact.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| `network_airgap`                    | When **network_airgap** is set **true**, <br/> 1. If Bridge exists in the default `$home/synopsys-bridge` or Bridge exists in the specified `synopsys_bridge_install_directory`, it is used. Otherwise, errors out. <br/> 2. When `synopsys_bridge_download_url` provided by user, <br/> a. If Bridge version available in `synopsys_bridge_download_url` is the same version that already exists in default `$home/synopsys-bridge`, it is used.<br/> b. If not, Bridge version pointed to by `synopsys_bridge_download_url` is downloaded to the default `$home/synopsys-bridge` or user specified `synopsys_bridge_install_directory` and used/cached.<br/> c. If `synopsys_bridge_download_url` doesn't have version info, plugin will look for versions.txt file at the same download URL folder level. If versions.txt file is not found, Bridge is not cached. |           
#### Note:
- If **synopsys_bridge_download_version** or **synopsys_bridge_download_url** is not provided, the plugin will download and configure the latest version of Bridge.

# Synopsys Bridge Setup

The latest version of the Synopsys Bridge is available at: [Synopsys Bridge](https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/)

The latest version of Synopsys Bridge will be downloaded by default if user doesn't provide the specific released version in the `pipeline parameter/Global UI` or the installed version is not the latest version..

## Setting Up Synopsys Bridge Manually

If you are unable to download the Synopsys Bridge from our internet-hosted repository or have been directed by support or services to use a custom version of the Synopsys Bridge, you can either specify a custom URL or you can specify the `synopsys_bridge_install_directory` parameter to specify the location of the directory in which the Synopsys Bridge is pre-installed.

## Proxy Support

Proxy configuration in Jenkins pipelines can be done in several ways. Here are two common ways to declare proxy settings in Jenkins:  

1. Utilizing the 'environment' block in Jenkinsfile.   
    Configuring proxy settings using the environment block within a Jenkins Pipeline.  
    `environment { HTTP_PROXY = 'http://proxyIP:proxyPort' }`

2. Employing the 'export' keyword.      
   Configuring proxy settings using environment variables.   
   `export HTTP_PROXY=http://proxyIP:proxyPort`

Supporting the following environment variables.  
1. HTTP_PROXY:  
_Format_: http://user:password@proxyIP:proxyPort/
2. HTTPS_PROXY:  
   _Format_: https://user:password@proxyIP:proxyPort/    
3. NO_PROXY:     
   _Format_: Comma separated list of urls/addresses for which proxy is not used  
   Example:no_proxy="cern.ch,some.domain:8001,192.168.1.57"

**Note:**
- Proxy with auth: Users need to pass username and password for authentication.  
   Example: http://user:password@proxyIP:proxyPort/
- Proxy with no auth: Users do not need to pass anything for authentication.   
  Example: http://proxyIP:proxyPort/  
  ** If proxy configuration require authentication and agent need to run behind the proxy, user need to pass parameter with authentication data like  `-auth user_name:password` while connecting agent to controller.  
For more details, you can visit the following link,  
https://about.gitlab.com/blog/2021/01/27/we-need-to-talk-no-proxy/