package app.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import app.models.Body;
import app.models.PluginInstanceModel;

@RestController
public class InstanceController extends AbstractInstanceController {

    private static final int CREATION_ATTEMPTS = 3;

    /*
     * Overwritten Methods
     */

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> createInstance(
            final String token,
            final String identity,
            final List<PluginInstanceModel> listModel) throws Exception {

        return ResponseEntity.ok(
                Body.create(null));
    }

    @Override
    protected ResponseEntity<Body<PluginInstanceModel>> getInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {

        return ResponseEntity.ok(
                Body.create(null));
    }

    @Override
    protected ResponseEntity<Body<Void>> deleteInstance(
            final String token,
            final String identity,
            final String zone,
            final String name) throws Exception {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Body<List<PluginInstanceModel>>> listInstances(
            final String token,
            final String identity) throws Exception {

        return ResponseEntity.ok(
                Body.create(null));
    }

}
