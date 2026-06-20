# Reglas de ofuscación para Google Play Store
# Empaqueta todas las clases ofuscadas en un solo nivel para dificultar la ingeniería inversa
-repackageclasses ''

# Conservar los números de línea para poder leer los reportes de crasheos en la Play Console
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Mantener los modelos de datos serializados intactos (evita errores en bases de datos Room)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}