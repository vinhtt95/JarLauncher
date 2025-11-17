jpackage \
  --type app-image \
  --input target/modules \
  --module-path target/classes:target/modules \
  --name "Jar Launcher" \
  --module com.vinhtt.jarlauncher/com.vinhtt.jarlauncher.MainApplication \
  --java-options "-Dprism.lcdtext=false" \
  --vendor "vinhtt" \
  --app-version "1.0.0" \
  --mac-package-name "JarLauncher"