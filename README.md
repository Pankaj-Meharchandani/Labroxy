# JetLab

JetLab is a Jetpack Compose GitLab client focused on a fast, pleasant project workflow.

## What it does

- Connects to GitLab.com or a self-managed GitLab host with a personal access token.
- Shows the signed-in user and their recent membership projects.
- Searches projects from the dashboard.
- Opens a project detail view with open issues, open merge requests, recent commits, branch count, stars, forks, and open issue counts.
- Stores the host and token locally with Jetpack DataStore.

## Stack

- Kotlin
- Jetpack Compose with Material 3
- AndroidX Lifecycle ViewModel
- Jetpack DataStore
- Ktor client with kotlinx.serialization

## Run

Open this folder in Android Studio, let Gradle sync, then run the `app` configuration.

Create a GitLab personal access token with at least `read_api` scope. For private repositories and full project metadata, `read_repository` is also useful.
