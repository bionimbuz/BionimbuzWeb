package controllers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import play.data.binding.Binder;
import play.data.validation.Validation;
import play.db.Model;
import play.db.Model.Property;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;

public class BaseController extends CRUD {

    // Listeners priorities - @Before | @After | @Finally
    // -------
    public static final short MAX_PRIORITY = 0;
    public static final short HIGH_PRIORITY = 10;
    public static final short NORM_PRIORITY = 20;
    public static final short MIN_PRIORITY = 40;
    // HTTP methods
    // ~~~~~~~
    public static final String POST_METHOD = "POST";
    // Generic constants
    // -------
    public static final String CONNECTED_USER = "connectedUser";

    @Before
    public static void addType() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        renderArgs.put("type", type);
    }

    public static void index() {
        if (getControllerClass() == CRUD.class) {
            forbidden();
        }
        render("CRUD/index.html");
    }

    public static void list(int page, String search, String searchFields, String orderBy, String order) {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        if (page < 1) {
            page = 1;
        }
        final List<Model> objects = type.findPage(page, search, searchFields, orderBy, order, (String) request.args.get("where"));
        final Long count = type.count(search, searchFields, (String) request.args.get("where"));
        final Long totalCount = type.count(null, null, (String) request.args.get("where"));
        try {
            render(type, objects, count, totalCount, page, orderBy, order);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
        }
    }

    public static void show(String id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Model object = type.findById(id);
        notFoundIfNull(object);
        try {
            render(type, object);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/show.html", type, object);
        }
    }

    @SuppressWarnings("deprecation")
    public static void attachment(String id, String field) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Model object = type.findById(id);
        notFoundIfNull(object);
        final Field reflectField = object.getClass().getDeclaredField(field);
        reflectField.setAccessible(true);
        final Object att = reflectField.get(object);
        if (att instanceof Model.BinaryField) {
            final Model.BinaryField attachment = (Model.BinaryField) att;
            if (attachment == null || !attachment.exists()) {
                notFound();
            }
            response.contentType = attachment.type();
            renderBinary(attachment.get(), attachment.length());
        }
        // DEPRECATED
        if (att instanceof play.db.jpa.FileAttachment) {
            final play.db.jpa.FileAttachment attachment = (play.db.jpa.FileAttachment) att;
            if (attachment == null || !attachment.exists()) {
                notFound();
            }
            renderBinary(attachment.get(), attachment.filename);
        }
        notFound();
    }

    public static void save(String id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Model object = type.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        flash.success(Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void blank() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Model object = (Model) constructor.newInstance();
        try {
            render(type, object);
        } catch (final TemplateNotFoundException e) {
            render("CRUD/blank.html", type, object);
        }
    }

    public static void create() throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        final Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (Validation.hasErrors()) {
            renderArgs.put("error", Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (final TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        flash.success(Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void delete(String id) throws Exception {
        final CustomObjectType type = CustomObjectType.get(getControllerClass());
        notFoundIfNull(type);
        final Model object = type.findById(id);
        notFoundIfNull(object);
        try {
            object._delete();
        } catch (final Exception e) {
            flash.error(Messages.get("crud.delete.error", type.modelName));
            redirect(request.controller + ".show", object._key());
        }
        flash.success(Messages.get("crud.deleted", type.modelName));
        redirect(request.controller + ".list");
    }

    protected static ObjectType createObjectType(Class<? extends Model> entityClass) {
        return new CustomObjectType(entityClass);
    }

    public static class CustomObjectType extends ObjectType {

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Constructors.
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        public CustomObjectType(final Class<? extends Model> modelClass) {
            super(modelClass);
        }

        public static CustomObjectType get(Class<? extends Controller> controllerClass) {

            return (CustomObjectType) ObjectType.get(controllerClass);
        }

        @Override
        public List<ObjectField> getFields() {
            final List<CustomObjectField> fields = new ArrayList<>();
            final List<ObjectField> hiddenFields = new ArrayList<>();
            for (final Model.Property f : this.factory.listProperties()) {
                final CustomObjectField of = new CustomObjectField(f);
                if (of.type != null) {
                    if (of.type.equals("hidden")) {
                        hiddenFields.add(of);
                    } else {
                        fields.add(of);
                    }
                }
            }
            hiddenFields.addAll(fields);
            return hiddenFields;
        }

        public static class CustomObjectField extends ObjectType.ObjectField {

            private String typeName = "unknown";
            private final Property property;

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Constructors.
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            public CustomObjectField(final Property property) {
                super(property);
                this.property = property;
                this.typeName = property.type.getSimpleName().toString();
            }

            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Get
            // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            public String typeName() {
                return this.typeName;
            }

            public Property getProperty() {
                return this.property;
            }
        }
    }
}
