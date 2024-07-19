package net.configuration.serializable.impl.types;

import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.api.SerializationException;
import net.configuration.serializable.api.SerializedObject;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

public class XmlSerializedObject extends AbstractSerializedObject{

    private final Document data;
    protected static final String NO_UNIQUE_PATH = "There are mor than 1 elements at path ";

    public XmlSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
        this.xmlPrefix = clazz.getSimpleName();
        this.data = this.loadNewDocument();
    }

    public XmlSerializedObject(@NotNull Document document, @NotNull Class<?> forClass){
        super(forClass);
        this.data = document;
        this.xmlPrefix = forClass.getSimpleName();
    }

    public XmlSerializedObject(@NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);

        this.data = this.loadNewDocument();
        this.xmlPrefix = clazz.getSimpleName();
    }

    @SuppressWarnings("unused")
    public XmlSerializedObject(){
        super();
        this.xmlPrefix = "dummy-";
        this.data = this.loadNewDocument();
    }

    public XmlSerializedObject(Document data){
        this.data = data;
        this.xmlPrefix = "dummy-";
    }

    @Override
    public Optional<Byte> getByte(@NotNull String name) {
        return Optional.of(Byte.parseByte(this.readFromUniquePath(xmlPrefix + name)));
    }

    @Override
    public void setByte(@NotNull String name, byte value) {
        this.updateUniquePathValue(this.xmlPrefix + name, value);
    }

    @Override
    public Optional<Short> getShort(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setShort(@NotNull String name, short value) {

    }

    @Override
    public Optional<Integer> getInt(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setInt(@NotNull String name, int value) {

    }

    @Override
    public Optional<Long> getLong(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setLong(@NotNull String name, long value) {

    }

    @Override
    public Optional<Float> getFloat(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setFloat(@NotNull String name, float value) {

    }

    @Override
    public Optional<Double> getDouble(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setDouble(@NotNull String name, double value) {

    }

    @Override
    public Optional<Character> getChar(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setChar(@NotNull String name, char value) {

    }

    @Override
    public Optional<String> getString(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setString(@NotNull String name, @NotNull String value) {

    }

    @Override
    public Optional<Boolean> getBoolean(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setBoolean(@NotNull String name, boolean value) {

    }

    @Override
    public <T extends Enum<T>> Optional<T> getEnum(@NotNull String name, @NotNull Class<? extends Enum<?>> classOfT) {
        return Optional.empty();
    }

    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {

    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        return Optional.empty();
    }

    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {

    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {

    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setNull(@NotNull String name) {

    }

    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        return false;
    }

    @Override
    public Optional<Collection<Integer>> getIntList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setIntList(@NotNull String name, @NotNull Collection<Integer> value) {

    }

    @Override
    public Optional<Collection<Long>> getLongList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setLongList(@NotNull String name, @NotNull Collection<Long> value) {

    }

    @Override
    public Optional<Collection<Double>> getDoubleList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setDoubleList(@NotNull String name, @NotNull Collection<Double> value) {

    }

    @Override
    public Optional<Collection<Byte>> getByteList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setByteList(@NotNull String name, @NotNull Collection<Byte> value) {

    }

    @Override
    public Optional<Collection<String>> getStringList(@NotNull String name) {
        return Optional.empty();
    }

    @Override
    public void setStringList(@NotNull String name, @NotNull Collection<String> value) {

    }

    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        return Optional.empty();
    }

    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {

    }

    @Override
    public byte @NotNull [] toByteArray() {
        return new byte[0];
    }

    @Override
    public void writeToStream(@NotNull OutputStream stream) {

    }

    @Override
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        return Optional.empty();
    }

    @NotNull
    private Document loadNewDocument(){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            return dbBuilder.newDocument();

        } catch (ParserConfigurationException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Get the {@link Node} that is referenced by the given path.
     *
     * @param path A path to that object, that may contain child references.
     * @return The underlying {@link Node} at the given path.
     */
    private Node getUniqueElementByTagName(String path){
        if(path.contains(".")){
            String[] sub = path.split("\\.");
            NodeList currentList = this.data.getElementsByTagName(sub[0]);
            if(currentList.getLength() > 0)
                throw new IllegalArgumentException("Not a unique path: " + path);

            Node current = currentList.item(0);
            for(int i = 1; i < sub.length; i++){
                Node item = currentList.item(0);
                NodeList children = item.getChildNodes();
                for(int j = 0; j < children.getLength(); j++){
                    Node child = children.item(j);
                    if(child.getNodeName().equals(sub[i])){
                        currentList = child.getChildNodes();
                        current = child;
                        break;
                    }
                }
            }
            return current;

        }else{
            NodeList list = this.data.getElementsByTagName(path);
            if(list.getLength() > 1)
                throw new IllegalStateException(NO_UNIQUE_PATH + path);

            return list.item(0);
        }
    }

    /**
     * Get the string value of the given element with unique tag {@code path}.
     *
     * @param path The unique tag name of the element.
     * @return The content text value of the element.
     */
    private String readFromUniquePath(String path){
        Node item = this.getUniqueElementByTagName(path);
        if(item.getNodeType() == Node.ELEMENT_NODE){
            Element elem = (Element) item;
            return elem.getTextContent();
        }

        throw new IllegalStateException("Illegal node at path " + path);
    }

    private void updateUniquePathValue(String path, Object value){
        Node item = this.getUniqueElementByTagName(path);
        if(item == null){
            //add new element
            Element e = this.data.createElement(path);
            e.setTextContent(value.toString());
            this.data.appendChild(e);


        }else if(item.getNodeType() == Node.ELEMENT_NODE){
            Element elem = (Element) item;
            elem.setTextContent(value.toString());
        }

    }

}
