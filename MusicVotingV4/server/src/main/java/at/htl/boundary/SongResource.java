package at.htl.boundary;

import at.htl.control.ArtistRepository;
import at.htl.control.SongRepository;
import at.htl.entity.Artist;
import at.htl.entity.Song;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("songs")
public class SongResource {

    @Inject
    SongRepository songRepository;

    @Inject
    ArtistRepository artistRepository;

    @GET
    public List<Song> getPlaylist(){
        return songRepository.getPlaylist();
    }

    @GET
    @Path("getNextSong")
    @Transactional
    public Song getNextSong(){
        List<Song> songs = songRepository.getPlaylist();

        if(songs.size() == 0){
            return null;
        }

        songRepository.deleteById(songs.get(0).getId());
        return songs.get(0);
    }


    @GET
    @Transactional
    @Path("/addSong/{artistName}/{songTitle}")
    //Es wird angenommen das der genaue Titel des Liedes übergeben wird
    public Response addSong(@PathParam("artistName") String artistName,
                            @PathParam("songTitle") String songTitle){

        Artist a = artistRepository.getArtist(artistName);
        if(a == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Song newSong = songRepository.getSongsWithSearch(
                a,
                songTitle
        ).get(0);

        if (newSong == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(songRepository.count("songid", newSong.getSongId()) > 0){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        songRepository.persist(newSong);

        return Response.ok().build();
    }

    @GET
    @Path("search/{artistName}/{songTitle}")
    public Response getSongsWithSearch(
            @PathParam("artistName") String artistName,
            @PathParam("songTitle") String songTitle){

        Artist a = artistRepository.getArtist(artistName);
        if(a == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(songRepository.
                getSongsWithSearch(
                        a,
                        songTitle)).build();
    }

    @GET
    @Path("search/{artistName}")
    public Response getSongsWithSearch(
            @PathParam("artistName") String artistName){

        Artist a = artistRepository.getArtist(artistName);
        if(a == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(
                songRepository.getSongs(a)).build();
    }
}
