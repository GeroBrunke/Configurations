package net.configuration.config.impl;

import net.configuration.advanced.Tuple;
import net.configuration.config.Configuration;
import net.configuration.config.ConfigurationException;
import net.configuration.network.SQLConnection;
import net.configuration.serializable.api.Creator;
import net.configuration.serializable.api.SerializableObject;
import net.configuration.serializable.impl.NullSerializable;
import net.configuration.serializable.impl.SerializationHelper;
import net.configuration.serializable.impl.types.SQLSerializedObject;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLConfiguration implements Configuration {

    @NotNull private final SQLConnection connection;
    @NotNull private final String table;
    @NotNull private final UUID key;

    @NotNull private final Map<String, String> data = new HashMap<>();

    @NotNull private Map<String, String> foreignKeys;
    @NotNull private final Map<String, Tuple<String, String>> complex = new HashMap<>();

    public SQLConfiguration(@NotNull SQLConnection con, @NotNull String table, @NotNull UUID key){
        this.connection = con;
        this.table = table;
        this.key = key;
        this.foreignKeys = this.getForeignKeys0();

        this.readFromTable();
    }

    /**
     * Delete the table entry that represents the instance of this object.
     *
     * @return True iff the entry was successfully deleted.
     */
    public boolean deleteEntry(){
        try{
            String sql = "DELETE FROM " + this.table + " WHERE id = '" + this.key + "'";
            this.connection.update(sql);
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete the table associated with this configuration.
     *
     * @return True iff the table was successfully deleted.
     */
    public boolean deleteTable(){
        try(PreparedStatement pst = connection.getConnection().prepareStatement("DROP TABLE " + this.table)){
            pst.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unused")
    public @NotNull Map<String, String> getForeignKeys() {
        return foreignKeys;
    }

    public @NotNull SQLConnection getConnection() {
        return connection;
    }

    @Override
    public boolean save() {
        this.createTable();
        this.writeToTable();
        return true;
    }

    @Override
    public boolean reload() {
        this.foreignKeys = this.getForeignKeys0();
        this.readFromTable();
        return true;
    }

    @Override
    public @NotNull String getName() {
        return table;
    }

    @Override
    public boolean hasMember(@NotNull String path) {
        return this.data.containsKey(path);
    }

    @Override
    public @NotNull Optional<Byte> getByte(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Byte.valueOf(this.data.get(path)));
    }

    @Override
    public void setByte(@NotNull String path, byte value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Integer> getInt(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Integer.valueOf(this.data.get(path)));
    }

    @Override
    public void setInt(@NotNull String path, int value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Long> getLong(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Long.valueOf(this.data.get(path)));
    }

    @Override
    public void setLong(@NotNull String path, long value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Short> getShort(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Short.valueOf(this.data.get(path)));
    }

    @Override
    public void setShort(@NotNull String path, short value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Float> getFloat(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Float.valueOf(this.data.get(path)));
    }

    @Override
    public void setFloat(@NotNull String path, float value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Double> getDouble(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Double.valueOf(this.data.get(path)));
    }

    @Override
    public void setDouble(@NotNull String path, double value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Character> getChar(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.data.get(path).charAt(0));
    }

    @Override
    public void setChar(@NotNull String path, char value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<Boolean> getBoolean(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(Boolean.valueOf(this.data.get(path)));
    }

    @Override
    public void setBoolean(@NotNull String path, boolean value) {
        this.data.put(path.replace(".", "-"), String.valueOf(value));
    }

    @Override
    public @NotNull Optional<String> getString(@NotNull String path) {
        path = path.replace(".", "-");
        if(!this.hasMember(path))
            return Optional.empty();

        return Optional.of(this.data.get(path));
    }

    @Override
    public void setString(@NotNull String path, String value) {
        this.data.put(path.replace(".", "-"), value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<List<T>> getList(@NotNull String path, @NotNull Class<T> classOfT) {
        path = path.replace(".", "-");

        if(!(this.hasMember(path)))
            return Optional.empty();

        String elem = this.getString(path).orElseThrow();
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            return Optional.of(this.getPrimitiveList(elem, classOfT));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            List<T> res = new ArrayList<>();
            String[] d = elem.split(",");
            for(String e : d){
                String[] da = e.split(":");
                String keyData = da[0];
                String tableData = da[1];

                SQLSerializedObject nested = new SQLSerializedObject(this.connection, keyData, tableData, classOfT);
                SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(nested);
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
        path = path.replace(".", "-");

        if(list == null || list.isEmpty() || list.get(0) == null)
            throw new ConfigurationException("Cannot write an empty list or a list with null elements");

        Class<T> classOfT = (Class<T>) list.get(0).getClass();
        if(classOfT == String.class || ClassUtils.isPrimitiveOrWrapper(classOfT)){
            this.setString(path, this.convertPrimitiveList(list));

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            StringBuilder str = new StringBuilder();
            for(var e : list){
                SQLSerializedObject nested = new SQLSerializedObject(this.connection, e.getClass());
                ((SerializableObject) e).write(nested);
                nested.flush();

                str.append(",").append(nested.getPrimaryKey()).append(":").append(nested.getTableName());
            }
            str = new StringBuilder(str.substring(1));
            this.setString(path, str.toString());

        }else if(classOfT.isEnum()){
            this.setEnumList(path, list);

        }else{
            throw new ConfigurationException("Could not set list " + list + ". Invalid element type " + classOfT);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T[]> getArray(@NotNull String path, @NotNull Class<T> classOfT) {
        var opt = this.getList(path, classOfT);
        if(opt.isEmpty())
            return Optional.empty();

        List<T> list = opt.get();
        T[] array = (T[]) Array.newInstance(classOfT, list.size());
        return Optional.of(list.toArray(array));
    }

    @Override
    public <T> void setArray(@NotNull String path, T[] array) {
        this.setList(path, List.of(array));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> Optional<T> get(@NotNull String path, @NotNull Class<T> classOfT) {
        path = path.replace(".", "-");

        if(!this.hasMember(path))
            return Optional.empty();

        String elem = this.getString(path).orElseThrow();
        if(elem.equalsIgnoreCase("null"))
            return Optional.empty();

        //read a valid object
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            Object prim = Objects.requireNonNull(SerializationHelper.extractPrimitive(elem, classOfT));
            return Optional.of((T) prim);

        }else if(List.class.isAssignableFrom(classOfT)){
            throw new ConfigurationException("Cannot get a list this way. Use getList(..) instead.");

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            SQLSerializedObject obj = new SQLSerializedObject(this.connection, elem, SQLSerializedObject.getTableName(classOfT), classOfT);
            SerializableObject val = Creator.getCreator((Class<? extends SerializableObject>) classOfT).read(obj);
            return Optional.of((T) val);

        }else if(classOfT.isEnum()){
            return Optional.of(this.convertToEnum(elem, classOfT));

        }else if(classOfT.isArray()){
            throw new ConfigurationException("Cannot get an array this way. Use getList(..) instead.");

        }

        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void set(@NotNull String path, T value) {
        path = path.replace(".", "-");

        if(value == null){
            this.setString(path, "null");
            return;
        }

        Class<T> classOfT = (Class<T>) value.getClass();
        if(ClassUtils.isPrimitiveOrWrapper(classOfT) || classOfT == String.class){
            this.setString(path, String.valueOf(value));


        }else if(List.class.isAssignableFrom(classOfT)){
            this.setList(path, (List<?>) value);

        }else if(SerializableObject.class.isAssignableFrom(classOfT)){
            SQLSerializedObject obj = new SQLSerializedObject(classOfT);
            ((SerializableObject) value).write(obj);
            obj.flush();

            this.complex.put(path, new Tuple<>(obj.getTableName(), obj.getPrimaryKey().toString()));
            this.setString(path, obj.getPrimaryKey().toString());

        }else if(classOfT.isEnum()){
            this.setString(path, value.toString());

        }else if(classOfT.isArray()){
            this.setArray(path, (Object[]) value);

        }else{
            throw new ConfigurationException("Not a serializable object: " + classOfT);
        }
    }

    /**
     * Convert a list of primitive objects or strings into a unique string representation. For example, the integer list
     * [1, 2, 3, 4] is converted to the string "1, 2, 3, 4".
     *
     * @param list The primitive list to convert.
     * @return The string representation of the list.
     */
    protected String convertPrimitiveList(@NotNull List<?> list){
        StringBuilder entry = new StringBuilder();
        for(var e : list){
            entry.append(", ").append(e);
        }
        entry = new StringBuilder(entry.substring(2));

        return entry.toString();
    }

    /**
     * Retrieve the primitive list from its string representation. The string "1, 2, 3, 4" is converted to the
     * integer list [1, 2, 3, 4].
     *
     * @param elem The string representation of a primitive list.
     * @param classOfT The type of elements in the list.
     * @return The actual java representation of the list.
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> getPrimitiveList(@NotNull String elem, @NotNull Class<T> classOfT){
        String[] d = elem.split(", ");
        List<T> res = new ArrayList<>();
        for(String e : d){
            res.add((T) SerializationHelper.extractPrimitive(e, classOfT));
        }

        return res;
    }

    /**
     * Insert the given enum value list into this object by creating a unique string representation of the enum
     * name list. For example, the list of colors [RED, GREEN, BLUE] is converted to "RED, GREEN, BLUE".
     *
     * @param path The path where the list is inserted in this object.
     * @param list The actual list of enum values to insert.
     */
    protected void setEnumList(@NotNull String path, @NotNull List<?> list){
        List<String> names = new ArrayList<>();
        for(var e : list){
            names.add(((Enum<?>) e).name());
        }
        this.setString(path, this.convertPrimitiveList(names));
    }

    /**
     * Retrieve the enum list from the list of enum value names. For example the string list ["RED", "BLUE", "GREEN"] is
     * converted to the actual enum list [RED, BLUE, GREEN].
     *
     * @param names The list of enum names.
     * @param classOfT The enum class to convert to.
     * @return The list of actual enums represented by the given names list.
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> getEnumList(@NotNull List<String> names, @NotNull Class<T> classOfT){
        List<T> res = new ArrayList<>(names.size());
        for(var e : names){
            try {
                T val = (T) classOfT.getMethod("valueOf", String.class).invoke(null, e);
                res.add(val);
            } catch (Exception ex) {
                throw new ConfigurationException(ex);
            }
        }

        return res;
    }

    /**
     * Convert the given string to the actual enum value with that given name.
     *
     * @param elem The name of the enum value.
     * @param classOfT The enum type represented by the name.
     * @return The actual enum value of given type with that name.
     */
    @NotNull
    @SuppressWarnings("unchecked")
    private <T> T convertToEnum(@NotNull String elem, @NotNull Class<T> classOfT){
        try {
            return (T) classOfT.getMethod("valueOf", String.class).invoke(null, elem);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Create the SQL table based on the names and values provided by the underlying data map.
     */
    private void createTable(){
        if(!this.connection.isConnected())
            this.connection.connect();

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + table + "(id CHAR(36) NOT NULL");
        List<String> fkeys = new ArrayList<>();
        for(var e : this.data.keySet()){
            String sqlType = "LONGTEXT";
            if(this.complex.containsKey(e)){
                var value = this.complex.get(e);
                fkeys.add("FOREIGN KEY (" + e + ") REFERENCES " + value.getKey() + "(id)");
                sqlType = "CHAR(36)";
            }

            sql.append(",").append(e).append(" ").append(sqlType);
        }

        sql.append(", PRIMARY KEY(id)");
        for(String e : fkeys){
            sql.append(", ").append(e);
        }
        sql.append(")");

        this.connection.update(sql.toString());
    }

    /**
     * Insert the current configuration data into the linked SQL table.
     */
    private void writeToTable(){
        String sql = "INSERT INTO " + this.table + "(types) VALUES (values)";

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        names.append(", id");
        values.append(", '").append(this.key).append("'");

        for(var entry : this.data.entrySet()){
            String name = entry.getKey();
            String value = entry.getValue();
            if(value != null && !value.equals(NullSerializable.CODON)){
                names.append(", ").append(name);
                values.append(", '").append(value).append("'");
            }
        }


        names = new StringBuilder(names.substring(2));
        values = new StringBuilder(values.substring(2));

        sql = sql.replace("types", names.toString()).replace("values", values.toString());
        this.connection.update(sql);
    }

    /**
     * Read this' configs values from the linked table where the id column has the same value as this' instance's
     * UUID.
     */
    private void readFromTable(){
        if(!this.connection.isConnected())
            this.connection.connect();

        try(PreparedStatement pst = this.connection.getConnection().prepareStatement("SELECT * FROM " + this.table
                + " WHERE id = '" + this.key + "'")){

            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                int coulmnCnt = rs.getMetaData().getColumnCount();
                for(int i = 2; i <= coulmnCnt; i++){
                    String name = rs.getMetaData().getColumnName(i);
                    String value = rs.getString(i);

                    this.data.put(name, value);
                }
            }

        }catch(SQLException e){
            //table does not exist
        }
    }

    /**
     * Fetch all the foreign key constraints from the linked table.
     *
     * @return A map containing the found foreign keys mapped to the table the key is pointing to.
     */
    @NotNull
    private Map<String, String> getForeignKeys0() {
        Map<String, String> map = new HashMap<>();
        if(!this.connection.isConnected())
            return map;

        try{
            DatabaseMetaData meta = this.connection.getConnection().getMetaData();
            ResultSet rs = meta.getImportedKeys(this.connection.getConnection().getCatalog(), null, this.table);
            while(rs.next()){
                String tableName = rs.getString("PKTABLE_NAME");
                String name = rs.getString("FKCOLUMN_NAME");
                map.put(name, tableName);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return map;
    }

}
