# Bảo vệ mã Java
-keep class com.example.demo_app.** { *; }

# Bảo vệ mã của thư viện Android
-keep class androidx.** { *; }
-keep class android.support.** { *; }

# Bảo vệ cấu trúc lớp Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Bảo vệ cấu trúc lớp Serializable
-keepclassmembers class * implements java.io.Serializable {
  static final long serialVersionUID;
  private static final java.io.ObjectStreamField[] serialPersistentFields;
  private void writeObject(java.io.ObjectOutputStream);
  private void readObject(java.io.ObjectInputStream);
  java.lang.Object writeReplace();
  java.lang.Object readResolve();
}

# Bảo vệ cấu trúc lớp SQLiteOpenHelper
-keep class * extends android.database.sqlite.SQLiteOpenHelper {
  public <init>(android.content.Context);
  public <init>(android.content.Context, java.lang.String, android.database.sqlite.SQLiteDatabase$CursorFactory, int);
  public void onCreate(android.database.sqlite.SQLiteDatabase);
  public void onUpgrade(android.database.sqlite.SQLiteDatabase, int, int);
}

# Bảo vệ cấu trúc lớp Cursor
-keep class android.database.Cursor { *; }
