package welcome.on.oracle.day;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/participant")
public class ParticipantResource {

    public static class Participant {
        String name;
        int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @GET
    @Produces("application/json")
    public Collection<Participant> getParticipants() {
        return null;
    }

    @GET
    @Produces("application/json")
    @Path("{name}")
    public Participant getParti(@PathParam("name") String name) {

    }
}
