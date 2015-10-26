/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infrastructure;

import Util.Hasher;

/**
 *
 * @author billaros
 */
public class Service {

    String URI;
    String Description;
    Aggregator Owner;
    String canonURI;
    String id;

    public String getCanonURI() {
        return canonURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCanonURI(String canonURI) {
        this.canonURI = canonURI;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public Aggregator getOwner() {
        return Owner;
    }

    public void setOwner(Aggregator Owner) {
        this.Owner = Owner;
    }

    public Service(String URI, String canonUri, String Description, Aggregator Owner) {
        this.URI = URI;
        this.Description = Description;
        this.Owner = Owner;
        this.canonURI = canonUri;
        String buf = Hasher.generateRandomPassword(10);
        setId(buf);
    }

}
