# JetLab

JetLab is a Jetpack Compose GitLab client focused on a fast, pleasant project workflow.

## What it does

- Connects to GitLab.com or a self-managed GitLab host with a personal access token.
- Shows a "Your work" home with Projects, Groups, Work items, Assigned work, Merge requests, To-Do List, and Notifications.
- Lists all accessible projects through GitLab pagination instead of only recent membership projects.
- Searches projects, groups, and work items from their sections.
- Opens a project detail view with open issues, open merge requests, recent commits, issue boards, branch count, stars, forks, and open issue counts.
- Uses Android back handling to return from project detail or a secondary work section before leaving the app.
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
