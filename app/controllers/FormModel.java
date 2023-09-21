package models;

import play.data.validation.Constraints;

public class FormModel {
    @Constraints.Required
    public String tema;
    @Constraints.Required
    public String tipo;
    @Constraints.Required
    public String cantidad;
    @Constraints.Required
    public String tono;
    @Constraints.Required
    public String opcionHashtag;
    @Constraints.Required
    public String hashtags;

}
