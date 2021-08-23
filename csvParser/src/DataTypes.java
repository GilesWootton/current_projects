/*************************************************
 * enum DataTypes
 * public so in own class
 **************************************************/
public enum DataTypes
{
    INT("INT"),
    DOUBLE("DOUBLE"),
    STRING("VARCHAR(255)");
    String dataTypeName;

    DataTypes(String s)
    {
        dataTypeName = s;
    }
}
