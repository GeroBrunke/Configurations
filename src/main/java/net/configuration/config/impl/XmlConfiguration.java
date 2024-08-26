package net.configuration.config.impl;

import net.configuration.config.ConfigurationException;
import net.configuration.config.FileConfiguration;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.impl.SerializationHelper;
import net.configuration.serializable.impl.types.XmlSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class XmlConfiguration extends FileConfiguration {

    private Element config;

    protected XmlConfiguration(File file) throws IOException {
        super(file);

        if(Files.size(file.toPath()) == 0){
            this.config = new Element("config");

        }else{
            this.config = this.loadDocument();
        }
    }

    @Override
    public boolean save() {
        this.removeParent(this.config);

        Document doc = new Document();
        doc.setRootElement(this.config);
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());

        try(FileOutputStream fos = new FileOutputStream(this.file)){
            out.output(doc, fos);
        }catch(IOException e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean reload() {
        try{
            this.config = this.loadDocument();
            return true;

        }catch(ConfigurationException e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean hasMember(@NotNull String path) {
        if(!path.contains(".")){
            return this.config.getChild(path) != null;
        }

        String[] d = path.split("\\.");
        Element current = this.config.getChild(d[0]);
        for(int i = 1; i < d.length; i++){
            current = current.getChild(d[i]);
            if(current == null)
                return false;
        }

        return current != null;
    }

    @Override
    public @NotNull Optional<Byte> getByte(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Byte.valueOf(this.getElement0(path).getText()));
    }

    @Override
    public void setByte(@NotNull String path, byte value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Integer> getInt(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Integer.valueOf(this.getElement0(path).getText()));
    }

    @Override
    public void setInt(@NotNull String path, int value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Long> getLong(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Long.valueOf(this.getElement0(path).getText()));
    }

    @Override
    public void setLong(@NotNull String path, long value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Short> getShort(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Short.valueOf(this.getElement0(path).getText()));
    }

    @Override
    public void setShort(@NotNull String path, short value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Float> getFloat(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Float.valueOf(this.getElement0(path).getText()));
    }

    @Override
    public void setFloat(@NotNull String path, float value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Double> getDouble(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Double.valueOf(this.getElement0(path).getText()));
    }

    @Override
    public void setDouble(@NotNull String path, double value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Character> getChar(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getElement0(path).getText().charAt(0));
    }

    @Override
    public void setChar(@NotNull String path, char value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Boolean.valueOf(this.getElement0(path).getText()));
    }

    @Override
    public void setBoolean(@NotNull String path, boolean value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<String> getString(@NotNull String path) {
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.getElement0(path).getText());
    }

    @Override
    public void setString(@NotNull String path, String value) {
        this.setContent(this.getElement(path), String.valueOf(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!(this.hasMember(path)))
            return Optional.empty();

        Element elem = this.getElement0(path);
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            return Optional.of(this.getPrimitiveList(elem, classOfT));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            List<T> res = new ArrayList<>();
            for(Element child : elem.getChildren()){
                XmlSerializedObject obj = new XmlSerializedObject( child.getChild(classOfT.getSimpleName()), classOfT);
                SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
                res.add((T) val);
            }

            return Optional.of(res);

        }else if(classOfT.isEnum()){
            List<String> names = this.getPrimitiveList(elem, String.class);
            return Optional.of(this.getEnumList(names, classOfT));

        }else{
            throw new ConfigurationException("Could not read list. Invalid element type " + classOfT);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void setList(@NotNull String path, List<T> list) {
        if(list == null || list.isEmpty() || list.get(0) == null)
            throw new ConfigurationException("Cannot write an empty list or a list with null elements");

        Class<T> classOfT = (Class<T>) list.get(0).getClass();
        Element elem = this.getElement(path);
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            this.setPrimitiveList(elem, list);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            int idx = 0;
            elem.removeContent();

            for(var e : list){
                XmlSerializedObject obj = new XmlSerializedObject(classOfT);
                ((SerializableObject) e).write(obj);
                obj.flush();

                Element listElem = new Element("Elem-" + idx++);
                listElem.addContent((Element) this.getDataFieldFromSerializedObject(obj));
                elem.addContent(listElem);
            }

        }else if(classOfT.isEnum()){
            List<String> names = new ArrayList<>();
            for(var e : list){
                Enum<?> en = (Enum<?>) e;
                names.add(en.name());
            }
            this.setPrimitiveList(elem, names);

        }else{
            throw new ConfigurationException("Could not set list " + list + ". Invalid element type " + classOfT);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT) {
        if(!this.hasMember(path))
            return Optional.empty();

        Element elem = this.getElement0(path);
        if(elem.getText().equalsIgnoreCase("null"))
            return Optional.empty();

        //read a valid object
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            Object prim = Objects.requireNonNull(SerializationHelper.extractPrimitive(elem.getText(), classOfT));
            return Optional.of((T) prim);

        }else if(List.class.isAssignableFrom(classOfT)){
            throw new ConfigurationException("Cannot get a list this way. Use getList(..) instead.");

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            XmlSerializedObject obj = new XmlSerializedObject(elem.getChild(classOfT.getSimpleName()), classOfT);
            SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
            return Optional.of((T) val);

        }else if(classOfT.isEnum()){
            try {
                T val = (T) classOfT.getMethod("valueOf", String.class).invoke(null, elem.getText());
                return Optional.of(val);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ConfigurationException(e);
            }

        }else if(classOfT.isArray()){
            throw new ConfigurationException("Cannot get an array this way. Use getList(..) instead.");

        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(@NotNull String path, T value) {
        if(value == null){
            this.setContent(this.getElement(path), "null");
            return;
        }

        Class<T> classOfT = (Class<T>) value.getClass();
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            this.setContent(this.getElement(path), String.valueOf(value));

        }else if(List.class.isAssignableFrom(classOfT)){
            this.setList(path, (List<?>) value);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            XmlSerializedObject obj = new XmlSerializedObject(classOfT);
            ((SerializableObject) value).write(obj);
            obj.flush();

            Element elem = this.getElement(path);
            elem.removeContent();
            this.removeParent(elem);
            elem.addContent((Element) this.getDataFieldFromSerializedObject(obj));
            this.config.addContent(elem);

        }else if(classOfT.isEnum()){
            this.setContent(this.getElement(path), value.toString());

        }else if(classOfT.isArray()){
            this.setArray(path, (Object[]) value);

        }else{
            throw new ConfigurationException("Not a serializable object: " + classOfT);
        }
    }

    @Override
    public String toString() {
        return this.toPrettyString();
    }

    /**
     * Get the XML element associated with the given path. If there is no element mapped to the given path
     * then a new one is created and linked to the provided path.
     *
     * @param path The path to the XML element.
     * @return The XML element linked to the path.
     */
    @NotNull
    protected Element getElement(@NotNull String path){
        return this.hasMember(path) ? this.getElement0(path) : this.createNewElement(path);
    }

    /**
     * Load the config values from the underlying file and return the root element of the XML file.
     *
     * @return The root element of that XML config file.
     */
    protected Element loadDocument(){
        try(FileInputStream fis = new FileInputStream(this.file)){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            org.w3c.dom.Document w3cDocument = documentBuilder.parse(fis);
            return new DOMBuilder().build(w3cDocument).getRootElement();

        }catch(IOException | SAXException | ParserConfigurationException e){
            throw new ConfigurationException(e);
        }
    }

    /**
     * Get the deepest element of the given config path. For example, the element returned for the path "one.two.three"
     * is the child with the name "three" from the element "two" that itself is a child of the element "one".
     *
     * @param path The path to the element.
     * @return The element at the end of the given path.
     */
    @NotNull
    private Element getElement0(@NotNull String path){
        if(!path.contains(".")){
            return this.config.getChild(path);
        }

        String[] d = path.split("\\.");
        Element current = this.config.getChild(d[0]);
        for(int i = 1; i < d.length; i++){
            current = current.getChild(d[i]);
            if(current == null)
                throw new ConfigurationException("Invalid path: " + path);
        }

        return current;
    }

    /**
     * Create a new element under the given path. If the path contains parent dependencies (indicated with .) then the parent
     * at the second to last index has to exist. For example, if the element at "one.two.three.four" is created, then
     * the element at "one.two.three" has to exist beforehand or an exception will be thrown.
     *
     * @param path The path for the new element.
     * @return The newly created element.
     */
    @NotNull
    private Element createNewElement(@NotNull String path){
        Element parent;
        String name;
        if(!path.contains(".")){
            parent = this.config;
            name = path;

        }else{
            String[] d = path.split("\\.");
            parent = this.config.getChild(d[0]);
            for(int i = 1; i < d.length; i++){
                parent = parent.getChild(d[i]);
                if(parent == null)
                    throw new ConfigurationException("Invalid path: " + path);
            }
            name = d[d.length - 1];
        }

        Element newElem = new Element(name);
        parent.addContent(newElem);

        return newElem;
    }

    /**
     * Convert the current config state into a pretty and readable XML string.
     *
     * @return The pretty formatted XML string.
     */
    private String toPrettyString(){
        this.removeParent(this.config);

        Document doc = new Document();
        doc.setRootElement(this.config);
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        String str = out.outputString(doc);

        this.removeParent(this.config);
        return str;
    }

    /**
     * Update the content of the given element to the new value.
     *
     * @param elem The element to update.
     * @param value The new value for that element.
     */
    private void setContent(@NotNull Element elem, @NotNull String value){
        elem.removeContent();
        elem.addContent(value);
    }

    /**
     * Detach the given element from its parent if the parent exists.
     *
     * @param element The element to detach from its parent.
     */
    private void removeParent(@NotNull Element element){
        if(element.getParent() != null)
            element.getParent().removeContent(element);
    }

    /**
     * Set a list of primitive objects or strings inside the given element. For this, the list is converted to a unique
     * string representation that is then inserted as the element's value. As an example, the integer list [1, 2, 3, 4]
     * is converted to the string "1, 2, 3, 4".
     *
     * @param elem The element that holds the string version of that list.
     * @param list The primitive list to write.
     */
    private void setPrimitiveList(@NotNull Element elem, @NotNull List<?> list){
        StringBuilder entry = new StringBuilder();
        for(var e : list){
            entry.append(", ").append(e);
        }
        entry = new StringBuilder(entry.substring(2));

        elem.setText(entry.toString());
    }

    /**
     * Retrieve the primitve or string list from the given element. For this, the text value of the given element is
     * converted back to a java representation of a list. As an example, the string "1, 2, 3, 4" is converted to the
     * integer list [1, 2, 3, 4].
     *
     * @param elem The element that stores the list.
     * @param classOfT The type of the list elements.
     * @return The java representation of the primitive list.
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> getPrimitiveList(@NotNull Element elem, @NotNull Class<T> classOfT){
        String[] d = elem.getText().split(", ");
        List<T> res = new ArrayList<>();
        for(String e : d){
            T val = (T) SerializationHelper.extractPrimitive(e, classOfT);
            res.add(val);
        }

        return res;
    }

}
