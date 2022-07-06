package com.aprendiendo.springboot.app.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aprendiendo.springboot.app.model.entity.Cliente;
import com.aprendiendo.springboot.app.model.service.IClienteService;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final static String UPLOADS_FOLDER = "//home//diebo//uploads//";

    @RequestMapping(value = "/listar", method = RequestMethod.GET)
    public String listar(Model model) {
        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clienteService.findAll());
        return "listar";
    }

    @RequestMapping(value = "/form")
    public String crear(Map<String, Object> model) {

        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        model.put("titulo", "Formulario de Cliente");
        return "form";
    }

    @RequestMapping(value = "/form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

        Cliente cliente = null;

        if (id > 0) {
            cliente = clienteService.findOne(id);
            if (cliente == null) {
                flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD!");
                return "redirect:/listar";
            }
        } else {
            flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
            return "redirect:/listar";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Editar Cliente");
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario de Cliente");
            return "form";
        }
        if(!foto.isEmpty()){
            if(cliente.getId() != null
            && cliente.getId() > 0
            && cliente.getFoto() != null
            && cliente.getFoto().length() > 0){
                Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(cliente.getFoto() + " eliminada con exito!");
                File archivo = rootPath.toFile();

                if(archivo.exists() && archivo.canRead()){
                    archivo.delete();
                }
            }
//            Path directorioRecursos = Paths.get("src//main//resources//static/uploads");
//            String rootPath = directorioRecursos.toFile().getAbsolutePath();
            String rootPath = UPLOADS_FOLDER;
            try {
                byte[] bytes = foto.getBytes();
                Path rutaCompleta = Paths.get( rootPath + foto.getOriginalFilename());
                Files.write(rutaCompleta, bytes);
                flash.addFlashAttribute("info", "Has subido correctamente'" + foto.getOriginalFilename() + "'");
                cliente.setFoto(foto.getOriginalFilename());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con éxito!" : "Cliente creado con éxito!";

        clienteService.save(cliente);
        status.setComplete();
        flash.addFlashAttribute("success", mensajeFlash);
        return "redirect:listar";
    }

    @RequestMapping(value = "/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

        if (id > 0) {
            Cliente cliente = clienteService.findOne(id);

            clienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con éxito!");

            Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(cliente.getFoto() + " eliminada con exito!");
            File archivo = rootPath.toFile();

            if(archivo.exists() && archivo.canRead()){
                archivo.delete();
            }
        }
        return "redirect:/listar";
    }

    @GetMapping(value = "/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash){
        Cliente cliente = clienteService.findOne(id);
        if(cliente == null){
            flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
            return "redirect:/listar";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Detalle del cliente: " + cliente.getNombre());
        return "ver";
    }

    @GetMapping(value="/home/diebo/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
        Path pathFoto = Paths.get(UPLOADS_FOLDER).resolve(filename);
        log.info("pathFoto: "+pathFoto);
        Resource recurso = null;
        try {
            recurso = new UrlResource(pathFoto.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }
}