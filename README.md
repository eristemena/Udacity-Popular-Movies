# Popular Movies

## Overview

An Android app to help users find popular and recent movies. This project is part of Udacity Nanodegree project.

Movie informations are retrieved from TheMovieDb.org API.

## Pre-requisites

* Android SDK v23
* Android Build Tools v23.0.1
* Android Support Repository
* API Key from [themoviedb.org](http://themoviedb.org)

## Libraries

* [Picasso](http://square.github.io/picasso/), an Android library for image downloading and caching
* [Stetho](http://facebook.github.io/stetho/) for debugging, very helpful to debug database.

## How To Install

This project uses the Gradle build system. To build this project, use the "gradlew build" command or use "Import Project" in Android Studio.

This app uses [The Movie Database API](https://www.themoviedb.org/documentation/api) to retrieve movies. You must register and get your own API key and paste it to YOUR_API_KEY_HERE in `res/values/strings.xml`.
