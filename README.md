# Spotify Streamer #
This is the app for the first and second projects in the Udacity Android Nanodegree program. It is an app that lets users search for musicians in Spotify, see their top songs, and listen to 30-second previews. It makes use of the Spotify API for all of its data and audio streams.


## Phones ##
For phones the app makes use of three separate activities that flow from one to the next.


### Artist Search ###
A user can enter an artist to search for and the app will display the results in a list.

![spotify_streamer artist search activity screenshot](https://github.com/jrreed/spotify_streamer/blob/master/screenshots/spotify_streamer_artist_search_260x462.png)


### Top Songs ###
After tapping on an artist from the Artist Search activity the app will display a list of the artist's top songs.

![spotify_streamer artist track list activity screenshot](https://github.com/jrreed/spotify_streamer/blob/master/screenshots/spotify_streamer_artist_track_list_260x462.png)


### Song Playback ###
After tapping on a song the app will render all the information it has about the song in an audio player. The player allows the user to start and stop playback of a 30-second preview of the song, skip to the next or previous song in the Top Songs list, and provides a scrub-bar to let the user scrub the playback position.

![spotify_streamer track player activity screenshot](https://github.com/jrreed/spotify_streamer/blob/master/screenshots/spotify_streamer_track_player_260x462.png)


## Tablets ##
For tablets the app combines a Master Detail Flow with a Dialog Fragment to achieve the same functionality as the phone.

![spotify_streamer tablet artist and tracks master detail flow screenshot](https://github.com/jrreed/spotify_streamer/blob/master/screenshots/spotify_streamer_tablet_master_detail_flow_520x693.png)
![spotify_streamer tablet playback activity screenshot](https://github.com/jrreed/spotify_streamer/blob/master/screenshots/spotify_streamer_tablet_playback_520x693.png)
 
 
