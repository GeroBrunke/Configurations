package net.configuration.serializable.impl.types;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.configuration.main.Main;
import net.configuration.network.SQLConnection;
import net.configuration.serializable.api.*;
import net.configuration.serializable.impl.NullSerializable;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class SQLSerializedObject extends ByteSerializedObject {

    private static final String TABLE_PREFIX = "serializable_";
    @NotNull private final String tableName;
    @NotNull private final SQLConnection connection;
    @NotNull private final HashMap<String, Class<?>> types = new HashMap<>();
    @NotNull private Map<String, String> foreignKeys;
    @NotNull private final Map<String, SQLSerializedObject> complexObjects = new HashMap<>();

    @NotNull private final UUID primaryKey;

    @NotNull
    private static final Map<Class<?>, List<String>> sqlDataTypes = new HashMap<>();
    @NotNull
    private static final BiMap<Character, String> characterFunction = HashBiMap.create();

    static{
        sqlDataTypes.put(String.class,  List.of("LONGTEXT"));
        sqlDataTypes.put(Boolean.class, List.of("TINYINT(128)"));
        sqlDataTypes.put(Byte.class, List.of("TINYINT(255)"));
        sqlDataTypes.put(Short.class, List.of("SMALLINT(255)"));
        sqlDataTypes.put(Integer.class, List.of("INT(255)"));
        sqlDataTypes.put(Long.class, List.of("BIGINT(255)"));
        sqlDataTypes.put(Float.class, List.of("FLOAT(24)"));
        sqlDataTypes.put(Double.class, List.of("FLOAT(53)"));
        sqlDataTypes.put(Character.class, List.of("CHAR(2)"));
        sqlDataTypes.put(boolean.class, List.of("TINYINT(128)"));
        sqlDataTypes.put(byte.class, List.of("TINYINT(255)"));
        sqlDataTypes.put(short.class, List.of("SMALLINT(255)"));
        sqlDataTypes.put(int.class, List.of("INT(255)"));
        sqlDataTypes.put(long.class, List.of("BIGINT(255)"));
        sqlDataTypes.put(float.class, List.of("FLOAT(24)"));
        sqlDataTypes.put(double.class, List.of("FLOAT(53)"));
        sqlDataTypes.put(char.class, List.of("CHAR(2)"));
        sqlDataTypes.put(Date.class, List.of("DATE"));
        sqlDataTypes.put(java.sql.Date.class, List.of("DATE"));

        characterFunction.put('0', "zero");
        characterFunction.put('1', "one");
        characterFunction.put('2', "two");
        characterFunction.put('3', "three");
        characterFunction.put('4', "four");
        characterFunction.put('5', "five");
        characterFunction.put('6', "six");
        characterFunction.put('7', "seven");
        characterFunction.put('8', "eight");
        characterFunction.put('9', "nine");
    }

    /**
     * Deletes the given table using the provided connection.
     *
     * @param connection The connection to the SQL database.
     * @param tableName The name of the table to delete.
     * @return True if the table could be deleted, false otherwise.
     */
    public static boolean deleteTable(@NotNull SQLConnection connection, @NotNull String tableName){
        try(PreparedStatement pst = connection.getConnection().prepareStatement("DROP TABLE " + tableName)){
            pst.executeUpdate();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return the name of the SQL table, objects of this class are written to.
     *
     * @param clazz The type of the serialized object.
     * @return The SQL table name.
     */
    public static String getTableName(@NotNull Class<?> clazz){
        return TABLE_PREFIX + clazz.getSimpleName();
    }

    public SQLSerializedObject(@NotNull SQLConnection con, @NotNull Class<?> clazz) {
        super(clazz);
        this.tableName = TABLE_PREFIX + clazz.getSimpleName();
        this.connection = con;
        if(!this.connection.isConnected())
            this.connection.connect();

        this.primaryKey = UUID.randomUUID();
        this.foreignKeys = this.getForeignKeys();
    }

    public SQLSerializedObject(@NotNull SQLConnection con, @NotNull String key, @NotNull Class<?> forClass) {
        super(forClass);
        this.primaryKey = UUID.fromString(key);
        this.tableName = TABLE_PREFIX + forClass.getSimpleName();
        this.connection = con;
        if(!this.connection.isConnected() && !this.connection.connect()) {
            throw new SerializationException("Not connected to SQL");
        }

        try {
            this.foreignKeys = this.getForeignKeys();
            this.data = this.readFromTable();
        } catch (SQLException e) {
            throw new SerializationException(e);
        }
    }

    public SQLSerializedObject(@NotNull SQLConnection con, @NotNull String key, @NotNull String tableName, @NotNull Class<?> forClass) {
        super(forClass);
        this.primaryKey = UUID.fromString(key);
        this.tableName = tableName;
        this.connection = con;
        if(!this.connection.isConnected())
            this.connection.connect();

        try {
            this.foreignKeys = this.getForeignKeys();
            this.data = this.readFromTable();
        } catch (SQLException e) {
            throw new SerializationException(e);
        }
    }

    @SuppressWarnings("unused")
    public SQLSerializedObject(@NotNull SQLConnection con, @NotNull Class<?> clazz, @NotNull Logger warnLog, boolean printWarnings) {
        super(clazz, warnLog, printWarnings);
        this.tableName = TABLE_PREFIX + clazz.getSimpleName();
        this.connection = con;
        if(!this.connection.isConnected())
            this.connection.connect();

        this.primaryKey = UUID.randomUUID();
        this.foreignKeys = this.getForeignKeys();
    }

    @SuppressWarnings("unused") //Called via reflection API
    public SQLSerializedObject(@NotNull Class<?> clazz) {
        super(clazz);
        this.connection = Main.getDefaultConnection();
        if(!this.connection.isConnected())
            this.connection.connect();

        this.tableName = TABLE_PREFIX + clazz.getSimpleName();
        this.primaryKey = UUID.randomUUID();
        this.foreignKeys = this.getForeignKeys();

    }

    @SuppressWarnings("unused")
    protected SQLSerializedObject(){
        super();
        this.connection = Main.getDefaultConnection();
        if(!this.connection.isConnected())
            this.connection.connect();

        this.tableName = TABLE_PREFIX + "dummy";
        this.primaryKey = UUID.randomUUID();
        this.foreignKeys = this.getForeignKeys();
    }


    @Override
    public void setByte(@NotNull String name, byte value) {
        this.data.put(name, String.valueOf(value));
        this.types.put(name, byte.class);
    }


    @Override
    public void setShort(@NotNull String name, short value) {
        this.data.put(name, String.valueOf(value));
        this.types.put(name, short.class);
    }


    @Override
    public void setInt(@NotNull String name, int value) {
        this.data.put(name, String.valueOf(value));
        this.types.put(name, int.class);
    }


    @Override
    public void setLong(@NotNull String name, long value) {
        this.data.put(name, String.valueOf(value));
        this.types.put(name, long.class);
    }


    @Override
    public void setFloat(@NotNull String name, float value) {
        this.data.put(name, String.valueOf(value));
        this.types.put(name, float.class);
    }


    @Override
    public void setDouble(@NotNull String name, double value) {
        this.data.put(name, String.valueOf(value));
        this.types.put(name, double.class);
    }


    @Override
    public void setChar(@NotNull String name, char value) {
        this.data.put(name, String.valueOf(value));
        this.types.put(name, char.class);
    }


    @Override
    public void setString(@NotNull String name, @NotNull String value) {
        this.data.put(name, value);
        this.types.put(name, String.class);
    }


    @Override
    public void setBoolean(@NotNull String name, boolean value) {
        this.data.put(name, value ? "1" : "0");
        this.types.put(name, boolean.class);
    }


    @Override
    public <T extends Enum<T>> void setEnum(@NotNull String name, @NotNull T value) {
        this.data.put(name, value.name());
        this.types.put(name, String.class);
    }


    @Override
    public void setSerializable(@NotNull String name, @NotNull SerializableObject value) {
        SQLSerializedObject nested = new SQLSerializedObject(this.connection, value.getClass());
        value.write(nested);
        this.complexObjects.put(name, nested);

        UUID foreignKey = nested.getPrimaryKey();
        this.setComplex(name, foreignKey, value.getClass());

    }

    @Override
    public <T extends SerializableObject> Optional<T> getSerializable(@NotNull String name, @NotNull Class<T> classOfT) {
        if(!this.complexObjects.containsKey(name)){
            return Optional.empty();
        }

        return Optional.of(Creator.getCreator(classOfT).read(this.complexObjects.get(name)));
    }

    @Override
    public void set(@NotNull String name, @NotNull SerializedObject value) {
        if(!(value instanceof SQLSerializedObject))
            throw new SerializationException("Cannot write a non-sql object into a sql table");

        this.complexObjects.put(name, (SQLSerializedObject) value);
        UUID foreignKey = ((SQLSerializedObject) value).getPrimaryKey();
        this.setComplex(name, foreignKey, value.getForClass().orElseThrow());
    }

    @Override
    public Optional<SerializedObject> get(@NotNull String name) {
        return Optional.ofNullable(this.complexObjects.get(name));
    }


    @Override
    public void setList(@NotNull String name, @NotNull Collection<? extends SerializableObject> value) {
        StringBuilder ids = new StringBuilder();
        for(var e : value){
            SQLSerializedObject nested = new SQLSerializedObject(this.connection, e.getClass());
            e.write(nested);
            nested.flush();

            ids.append(",").append(nested.getPrimaryKey()).append(":").append(nested.tableName);
        }
        ids = new StringBuilder(ids.substring(1));
        this.setString(name, ids.toString());

    }

    @Override
    public Optional<Collection<SerializableObject>> getList(@NotNull String name, Class<? extends SerializableObject> clazz) {
        Optional<String> opt = this.getString(name);
        if(opt.isEmpty())
            return Optional.empty();

        List<SerializableObject> list = new ArrayList<>();
        for(String e : opt.get().split(",")){
            String[] data = e.split(":");
            String key = data[0];
            String table = data[1];

            SQLSerializedObject nested = new SQLSerializedObject(this.connection, key, table, getClass(this.connection, table, key));
            SerializableObject obj = Creator.getCreator(clazz).read(nested);
            list.add(obj);
        }

        return Optional.of(list);

    }

    @Override
    public boolean isNextNull(@NotNull String name, @NotNull Class<?> type) {
        if(type.isPrimitive())
            type = ClassUtils.primitiveToWrapper(type);

        Object read = this.data.get(name);
        if(read == null)
            read = this.complexObjects.get(name);

        boolean nullValue = read == null || read.toString().equals(NullSerializable.CODON);

        if(!nullValue){ //if not-null reset pointer
            this.fieldPointer.computeIfPresent(type, (key, value) -> fieldPointer.put(key, value-1));
        }

        return nullValue;
    }

    @Override
    public Optional<SerializableObject> getNull(@NotNull String name) {
        if(!this.data.containsKey(name))
            return Optional.empty();

        String val = this.data.get(name);
        if(val == null || val.equals(NullSerializable.CODON)){
            return Optional.of(new NullSerializable(name));
        }

        return Optional.empty();

    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> getRawObject(@NotNull String name, @NotNull Class<?> classOfT) {
        if(this.data.containsKey(name)){
            String elem = this.data.get(name);
            if(elem.equals(NullSerializable.CODON))
                return Optional.empty();

            return Optional.of(classOfT.cast(elem));

        }else if(this.complexObjects.containsKey(name)){
            SQLSerializedObject nested = this.complexObjects.get(name);
            Optional<?> o = nested.getSerializable(name, (Class<? extends SerializableObject>) classOfT);
            if(o.isPresent())
                return Optional.of(o.get());
        }

        return Optional.empty();
    }

    @Override
    public void flush() {
        super.flush();

        //write to table
        if(!this.connection.isConnected())
            this.connection.connect();

        this.createTable();
        this.writeToTable();

        this.restore();
    }

    @Override
    public byte @NotNull [] toByteArray() {
        return this.primaryKey.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return this.primaryKey.toString();
    }

    /**
     * Get the primary key of the stored object in the respective table. Note that this method will always return null
     * if the {@link SQLSerializedObject#flush()} method was not called once before.
     *
     * @return The primary key of the entry in the SQL table that stores the underlying object.
     */
    public @NotNull UUID getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @return The SQL table name of this object.
     */
    public @NotNull String getTableName() {
        return tableName;
    }

    /**
     * @return The SQL connection used for this object.
     */
    public @NotNull SQLConnection getConnection() {
        return connection;
    }

    private void setComplex(@NotNull String name, @NotNull UUID foreignKey, @NotNull Class<?> clazz){
        this.data.put(name, foreignKey.toString());
        this.types.put(name, clazz);
    }

    @NotNull
    private Map<String, String> readFromTable() throws SQLException{
        if(!this.connection.isConnected())
            this.connection.connect();

        Map<String, String> map = new HashMap<>();
        try(PreparedStatement pst = this.connection.getConnection().prepareStatement("SELECT * FROM " + this.tableName
                + " WHERE id = '" + this.primaryKey + "'")){

            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                int coulmnCnt = rs.getMetaData().getColumnCount();
                for(int i = 3; i <= coulmnCnt; i++){
                    String name = this.deserializeKey(rs.getMetaData().getColumnName(i));
                    String value = rs.getString(i);

                    if(this.foreignKeys.containsKey(name) && value != null){
                        String table = this.foreignKeys.get(name);
                        SQLSerializedObject nested = new SQLSerializedObject(this.connection, value, table, getClass(this.connection, table, value));
                        this.complexObjects.put(name, nested);

                    }else{
                        map.put(name, value);
                    }
                }
            }

        }

        return map;
    }


    private void createTable(){
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + this.tableName + "(id CHAR(36) NOT NULL, class VARCHAR(1024) NOT NULL");

        List<String> fkeys = new ArrayList<>();
        for(var entry : this.data.entrySet()){
            String key = this.serializeKey(entry.getKey());
            Class<?> type = this.types.get(entry.getKey());

            String sqlType;
            if(type != null){
                if(sqlDataTypes.get(type) == null){
                    //complex type, so use foreign key relationship
                    fkeys.add("FOREIGN KEY (" + key + ") REFERENCES " + TABLE_PREFIX + type.getSimpleName() + "(id)");
                    sqlType = "CHAR(36)";
                    sql.append(", ").append(key).append(" ").append(sqlType);

                }else{
                    sqlType = "LONGTEXT";
                    sql.append(", ").append(key).append(" ").append(sqlType);
                }
            }else{
                sqlType = "LONGTEXT";
                sql.append(", ").append(key).append(" ").append(sqlType);
            }

        }
        sql.append(", PRIMARY KEY(id)");
        for(String e : fkeys){
            sql.append(", ").append(e);
        }
        sql.append(")");

        //create tables for complex objects
        List<SQLSerializedObject> delayed = new ArrayList<>();
        for(var e : this.complexObjects.values()){
            var opt = e.getForClass();
            if(opt.isPresent()){
                if(opt.get() != this.clazz){
                    e.flush();

                }else{
                    //delay flushing of the object of the same type, since we want to establish an f_key self reference first
                    delayed.add(e);
                }
            }
        }

        this.connection.update(sql.toString());

        //write delayed objects into table
        for(var e : delayed){
            e.writeToTable();
            e.restore();
        }
    }

    private void writeToTable(){
        if(this.isPresent(this.primaryKey))
            return;

        String sql = "INSERT INTO " + this.tableName + "(types) VALUES (values)";

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        names.append(", id").append(", class");
        values.append(", '").append(this.primaryKey).append("'").append(", '").append(this.clazz.getName()).append("'");

        for(var entry : this.data.entrySet()){
            String name = this.serializeKey(entry.getKey());
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

    @NotNull
    private Map<String, String> getForeignKeys() {
        Map<String, String> map = new HashMap<>();
        if(!this.connection.isConnected())
            return map;

        try{
            DatabaseMetaData meta = this.connection.getConnection().getMetaData();
            ResultSet rs = meta.getImportedKeys(this.connection.getConnection().getCatalog(), null, this.tableName);
            while(rs.next()){
                String table = rs.getString("PKTABLE_NAME");
                String name = rs.getString("FKCOLUMN_NAME");
                map.put(this.deserializeKey(name), table);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return map;
    }

    @NotNull
    public static Class<?> getClass(@NotNull SQLConnection connection, @NotNull String table, @NotNull String key){
        try(PreparedStatement pst = connection.getConnection().prepareStatement("SELECT class FROM " + table + " WHERE id = '" + key + "'")){
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return Class.forName(rs.getString("class"));
            }

        }catch(SQLException | ClassNotFoundException e){
            throw new SerializationException(e);
        }

        throw new SerializationException("No class found in table " + table + " for key " + key);
    }

    @NotNull
    private String serializeKey(@NotNull String key){
        if(key.matches("-?\\d+")){ //regex for number
            StringBuilder result = new StringBuilder();
            for(int i = 0; i < key.length(); i++){
                String encoded = characterFunction.get(key.charAt(i));
                result.append("-").append(encoded);
            }
            return result.substring(1);
        }

        return key;
    }

    @NotNull
    private String deserializeKey(@NotNull String columnName){
        if(columnName.contains("-")){
            String[] encoded = columnName.split("-");
            char[] array = new char[encoded.length];
            for(int i = 0; i < encoded.length; i++){
                if(!characterFunction.containsValue(encoded[i]))
                    throw new SerializationException("Invalid column name");

                array[i] = characterFunction.inverse().get(encoded[i]);
            }

            return new String(array);

        }else{
            if(characterFunction.containsValue(columnName)){
                return String.valueOf(characterFunction.inverse().get(columnName));
            }

            return columnName;
        }
    }

    private void restore() {
        this.complexObjects.clear();
        this.data.clear();
        this.foreignKeys.clear();
        try {
            this.foreignKeys = this.getForeignKeys();
            this.data = this.readFromTable();
        } catch (SQLException e) {
            throw new SerializationException(e);
        }
    }

    private boolean isPresent(@NotNull UUID uuid){
        try(PreparedStatement pst = this.connection.getConnection().prepareStatement("SELECT COUNT(*) FROM " + this.tableName + " WHERE id = '" + uuid + "'")){
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                return rs.getInt(1) != 0;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }
}
