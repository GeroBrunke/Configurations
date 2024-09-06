package net.configuration.serializable.impl.types;

import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import net.configuration.serializable.impl.NullSerializable;
import net.configuration.serializable.impl.SerializationHelper;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class XmlSerializedObject extends AbstractSerializedObject{

    private static final String UNKNOWN_FIELD_PREFIX = "extra_";
    private static final String DUMMY_PREFIX = "root";

    private final Element data;

    public XmlSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
        this.xmlPrefix = clazz.getSimpleName();
        this.data = new Element(this.xmlPrefix);
    }

    public XmlSerializedObject(@NotNull Document doc, @NotNull Class<?> forClass){
        super(forClass);
        this.xmlPrefix = forClass.getSimpleName();
        this.data = doc.getRootElement();
    }

    public XmlSerializedObject(@NotNull Element rootElem, @NotNull Class<?> forClass){
        super(forClass);
        this.xmlPrefix = forClass.getSimpleName();
        this.data = rootElem;
    }

    @SuppressWarnings("unused")
    public XmlSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
        this.xmlPrefix = clazz.getSimpleName();
        this.data = new Element(this.xmlPrefix);
    }

    @SuppressWarnings("unused")
    public XmlSerializedObject(){
        super();
        this.xmlPrefix = DUMMY_PREFIX;
        this.data = new Element(this.xmlPrefix);
    }

    public XmlSerializedObject(Element data){
        this.xmlPrefix = DUMMY_PREFIX;
        this.data = data;
    }

    @Override
    public Optional<Byte> getByte(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(Byte.valueOf(this.get0(name)));
    }

    @Override
    public void setByte(@NotNull String name, byte value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    public Optional<Short> getShort(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(Short.valueOf(this.get0(name)));
    }

    @Override
    public void setShort(@NotNull String name, short value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    public Optional<Integer> getInt(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(Integer.valueOf(this.get0(name)));
    }

    @Override
    public void setInt(@NotNull String name, int value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    public Optional<Long> getLong(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(Long.valueOf(this.get0(name)));
    }

    @Override
    public void setLong(@NotNull String name, long value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    public Optional<Float> getFloat(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(Float.valueOf(this.get0(name)));
    }

    @Override
    public void setFloat(@NotNull String name, float value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    public Optional<Double> getDouble(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(Double.valueOf(this.get0(name)));
    }

    @Override
    public void setDouble(@NotNull String name, double value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    public Optional<Character> getChar(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(this.get0(name).charAt(0));
    }

    @Override
    public void setChar(@NotNull String name, char value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    public Optional<String> getString(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(this.get0(name));
    }

    @Override
    public void setString(@NotNull String name, @NotNull String value) {
        this.set0(name, value);
    }

    @Override
    public Optional<Boolean> getBoolean(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        return Optional.of(Boolean.valueOf(this.get0(name)));
    }

    @Override
    public void setBoolean(@NotNull String name, boolean value) {
        this.set0(name, String.valueOf(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        String enumName = this.get0(name);
        return Optional.of(Enum.valueOf((Class<T>) classOfT, enumName));
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {
        this.set0(name, value.name());
    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        Element nestedElem = this.data.getChild(name);
        XmlSerializedObject nested = new XmlSerializedObject(nestedElem, classOfT);
        Creator<T> creator = Creator.getCreator(classOfT);
        T val = creator.read(nested);

        return Optional.of(val);
    }

    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {
        XmlSerializedObject nested = new XmlSerializedObject(value.getClass());
        value.write(nested);
        nested.flush();

        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        Element e = new Element(name);
        rekAddChildren(nested.data, e);

        this.data.addContent(e);
    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        Element nestedElem = this.data.getChild(name);
        XmlSerializedObject nested = new XmlSerializedObject(nestedElem);
        return Optional.of(nested);
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {
        if(value instanceof XmlSerializedObject xml){
            if(StringUtils.isNumeric(name)){
                //field name was invalid, so add a specific prefix
                name = UNKNOWN_FIELD_PREFIX + name;
            }

            Element e = new Element(name);
            rekAddChildren(xml.data, e);

            this.data.addContent(e);
            return;
        }

        throw new SerializationException("Cannot write a non-xml object into a xml format");
    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        String codon = this.get0(name);
        if(!codon.equals(NullSerializable.CODON))
            return Optional.empty();

        return Optional.of(new NullSerializable(name));
    }

    @Override
    public void setNull(@NotNull String name) {
        this.set0(name, NullSerializable.CODON);
    }

    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        Object read = this.get0(name);
        boolean nullValue = read == null || read.toString().equals(NullSerializable.CODON);

        if(!nullValue){ //if not-null reset pointer
            this.fieldPointer.computeIfPresent(type, (key, value) -> fieldPointer.put(key, value-1));
        }

        return nullValue;
    }

    @Override
    public Optional<Collection<Integer>> getIntList(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        List<Integer> list = new ArrayList<>();
        String[] raw = this.get0(name).split(", ");
        for(String e : raw){
            list.add(Integer.valueOf(e));
        }

        return Optional.of(list);
    }

    @Override
    public void setIntList(@NotNull String name, @NotNull Collection<Integer> value) {
        this.set0(name, this.listToString(value));
    }

    @Override
    public Optional<Collection<Long>> getLongList(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        List<Long> list = new ArrayList<>();
        String[] raw = this.get0(name).split(", ");
        for(String e : raw){
            list.add(Long.valueOf(e));
        }

        return Optional.of(list);
    }

    @Override
    public void setLongList(@NotNull String name, @NotNull Collection<Long> value) {
        this.set0(name, this.listToString(value));
    }

    @Override
    public Optional<Collection<Double>> getDoubleList(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        List<Double> list = new ArrayList<>();
        String[] raw = this.get0(name).split(", ");
        for(String e : raw){
            list.add(Double.valueOf(e));
        }

        return Optional.of(list);
    }

    @Override
    public void setDoubleList(@NotNull String name, @NotNull Collection<Double> value) {
        this.set0(name, this.listToString(value));
    }

    @Override
    public Optional<Collection<Byte>> getByteList(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        List<Byte> list = new ArrayList<>();
        String[] raw = this.get0(name).split(", ");
        for(String e : raw){
            list.add(Byte.valueOf(e));
        }

        return Optional.of(list);
    }

    @Override
    public void setByteList(@NotNull String name, @NotNull Collection<Byte> value) {
        this.set0(name, this.listToString(value));
    }

    @Override
    public Optional<Collection<String>> getStringList(@NotNull String name) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        String[] raw = this.get0(name).split(", ");
        List<String> list = new ArrayList<>(Arrays.asList(raw));

        return Optional.of(list);
    }

    @Override
    public void setStringList(@NotNull String name, @NotNull Collection<String> value) {
        this.set0(name, this.listToString(value));
    }

    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        List<SerializableObject> list = new ArrayList<>();
        Element nestedElem = this.data.getChild(name);
        for(Element listElement : nestedElem.getChildren()){
            XmlSerializedObject nested = new XmlSerializedObject(listElement, clazz);
            Creator<?> creator = Creator.getCreator(clazz);
            SerializableObject val = creator.read(nested);

            list.add(val);
        }

        return Optional.of(list);
    }

    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {
        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        Element e = new Element(name);
        int idx = 0;
        for(var obj : value){
            Element listElem = new Element("Elem-" + idx++);
            XmlSerializedObject nested = new XmlSerializedObject(obj.getClass());
            obj.write(nested);
            nested.flush();

            rekAddChildren(nested.data, listElem);
            e.addContent(listElem);
        }

        this.data.addContent(e);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        if(this.doesNotContain0(name))
            return Optional.empty();

        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        Element raw = this.data.getChild(name);
        if(raw.getChildren().isEmpty()){
            String elem = raw.getText();
            if(elem.equals(NullSerializable.CODON))
                return Optional.empty();

            return Optional.ofNullable(SerializationHelper.extractPrimitive(elem, classOfT));

        }else{
            //read complex object
            Optional<? extends SerializableObject> opt = this.getSerializable(name, (Class<? extends SerializableObject>) classOfT);
            if(opt.isPresent())
                return Optional.of(opt.get());

        }

        return Optional.empty();
    }

    @Override
    public byte @NotNull [] toByteArray() {
        return this.toPrettyString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void writeToStream(@NotNull OutputStream stream) {
        String fileData = this.toPrettyString();
        try(OutputStreamWriter writer = new OutputStreamWriter(stream)){
            writer.write(fileData);
            writer.flush();

        }catch(IOException e){
            throw new SerializationException(e);
        }
    }

    @Override
    public String toString() {
        return this.toPrettyString();
    }

    /**
     * @return A pretty human-readable XML string representing this objects data.
     */
    private String toPrettyString(){
        if(this.data.getParent() != null)
            this.data.getParent().removeContent(this.data);

        Document doc = new Document();
        doc.setRootElement(this.data);
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        String str = out.outputString(doc);

        this.data.getParent().removeContent(this.data);
        return str;
    }

    /**
     * Add the element and all its children to the given writeTo element.
     *
     * @param elem The element and all its children to write.
     * @param writeTo The element to which the given element is attached.
     */
    private void rekAddChildren(@NotNull Element elem, Element writeTo){
        for(Element child : elem.getChildren()){
            Element newChild = new Element(child.getName());
            if(!child.getChildren().isEmpty()){
                rekAddChildren(child, newChild);
            }

            newChild.addContent(child.getText());
            writeTo.addContent(newChild);
        }
    }

    /**
     * Set the given name - value pair inside the root element of this object.
     *
     * @param name The field name.
     * @param value The actual field value to write.
     */
    private void set0(@NotNull String name, @NotNull String value){
        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        Element e = new Element(name);
        e.addContent(value);

        this.data.addContent(e);
    }

    /**
     * Retrieve the string representation of the value that is mapped to the given name.
     *
     * @param name The name the value is mapped to.
     * @return The value at the given name.
     */
    private String get0(@NotNull String name){
        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        return this.data.getChildText(name);
    }

    /**
     * Check if the current XML document does not contain the element of given name.
     *
     * @param name The name to check.
     * @return True iff the XML document does not contain an element with that name.
     */
    private boolean doesNotContain0(@NotNull String name){
        if(StringUtils.isNumeric(name)){
            //field name was invalid, so add a specific prefix
            name = UNKNOWN_FIELD_PREFIX + name;
        }

        return this.data.getChildText(name) == null;
    }

    /**
     * Convert a list of primitive objects into its string representation. For example, the integer list [1, 2, 3] is
     * converted to the string "1, 2, 3".
     *
     * @param list The primitive list to convert.
     * @return A string representation of the given primitive list.
     */
    private String listToString(Collection<?> list){
        StringBuilder str = new StringBuilder();
        for(var e : list){
            str.append(", ").append(e);
        }
        str = new StringBuilder(str.substring(2));

        return str.toString();
    }

}
