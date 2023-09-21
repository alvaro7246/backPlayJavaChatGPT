package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import services.GPTService;

import play.mvc.*;
import play.db.jpa.*;
import play.mvc.Http.Request;
import play.mvc.Http.RequestBody;
import play.libs.Json;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.data.Form;

import javax.inject.Inject;
import java.io.IOException;

import play.db.Database;
import java.sql.*;

public class GPTController extends Controller {

    private final GPTService gptService;
    private final Database db;
    private final FormFactory formFactory;
    @Inject
    public GPTController(GPTService gptService, Database db, FormFactory formFactory) {
        this.db = db;
        this.formFactory = formFactory;
        this.gptService = gptService;
    };
    //GENERA ID DE LA SIGUIENTE PUBLICACION
    public Result generarIDPubli(){
        try (Connection conn = db.getConnection();
             CallableStatement callableStatement = conn.prepareCall("{call spu_siguiente_publicacion()}")) {

            // Ejecuta el procedimiento
            callableStatement.execute();
            // Obtiene el resultado
            ResultSet resultSet = callableStatement.getResultSet();
            // Procesa el resultado y convierte a JSON
            ArrayNode resultArray = Json.newArray();
            while (resultSet.next()) {
                ObjectNode resultObject = Json.newObject();
                resultObject.put("id_publicacion", resultSet.getString("id_publicacion"));
                // Agrega más columnas según sea necesario
                resultArray.add(resultObject);
            }
            return ok(resultArray);
            } catch (SQLException e) {
            e.printStackTrace();
            return internalServerError("Error al ejecutar el procedimiento almacenado: " + e.getMessage());
        }
    }
    //GENERAR PUBLICACION PARA LINKEDIN CON CHATGPT
    public Result guardarDatos(Http.Request request) {
        // Recuperar el JSON enviado desde el frontend
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Se esperaba un objeto JSON");
        }
        System.out.println("JSON VALOR"+json.toString());
        String tema = json.get("tema").asText();
        String tipo = json.get("tipo").asText();
        int cantidad = json.get("cantidad").asInt();
        String tono = json.get("tono").asText();
        String opcionHashtag = json.get("opcionHashtag").asText();
        String hashtags = json.get("hashtags").asText();
        String id_pub = json.get("id_pub").asText();
        try (Connection conn = db.getConnection();
             CallableStatement callableStatement = conn.prepareCall("{call spu_insertar_publicacion(?, ?, ?, ?, ?, ?, ?)}")) {
            // Establece los parámetros del procedimiento
            callableStatement.setString(1, id_pub);
            callableStatement.setString(2, tema);
            callableStatement.setString(3, tipo);
            callableStatement.setInt(4, cantidad);
            callableStatement.setString(5, tono); 
            callableStatement.setString(6, opcionHashtag);
            callableStatement.setString(7, hashtags);
            // Ejecuta el procedimiento almacenado
            callableStatement.execute();

            return ok("Procedimiento almacenado ejecutado con éxito");
        } catch (SQLException e) {
            e.printStackTrace();
            return internalServerError("Error al ejecutar el procedimiento almacenado: " + e.getMessage());
        }
    }

    public Result generarRespuesta(Http.Request request) {
        // Recuperar el JSON enviado desde el frontend
        JsonNode json = request.body().asJson();
        if (json == null) {
          return badRequest("Se esperaba un objeto JSON");
        }
         String pregunta;
         String tema = json.get("tema").asText();
         String tipo = json.get("tipo").asText();
         int cantidad = json.get("cantidad").asInt();
         String tono = json.get("tono").asText();
         String opcionHashtag = json.get("opcionHashtag").asText();
         String hashtags = json.get("hashtags").asText();

        if(opcionHashtag.equals("No")){
            pregunta = "Genere una publicacion para Linkedin del tema "+ tema + " con tipo " + tipo + " con un tono " + tono + " sin incluir hashtags con "+ cantidad +" palabras en español";
        }
        else{
            if(opcionHashtag.equals("Si") && !hashtags.isEmpty()){
                pregunta = "Genere una publicacion para Linkedin del tema "+ tema + " con tipo " + tipo + " con un tono " + tono + " incluyendo estos hashtags " + hashtags + " con "+ cantidad +" palabras en español" ;
            }
            else{
                pregunta = "Genere una publicacion para Linkedin del tema "+ tema + " con tipo " + tipo + " con un tono " + tono + " incluyendo hashtags con "+ cantidad +" palabras en español" ;
            }
        }
        try {
            System.out.println(pregunta);
            String respuesta = gptService.generarTexto(pregunta, cantidad);
            return ok(respuesta);
        } catch (IOException e) {
            return internalServerError("Error al generar la respuesta.");
        }
    }
    //GENERAR TEXTO CON CHATGPT PREGUNTA PREDETERMINADA
    public Result generarRespuesta2() {
        try {
            String respuesta = gptService.generarTexto("Como cocinar arroz", 100);
            return ok(respuesta);
        } catch (IOException e) {
            return internalServerError("Error al generar la respuesta.");
        }
    }

}