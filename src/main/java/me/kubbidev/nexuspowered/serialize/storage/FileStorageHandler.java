package me.kubbidev.nexuspowered.serialize.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Optional;

/**
 * Utility class for handling storage file i/o. Saves backups of the data files on each save.
 *
 * @param <T> the type being stored
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class FileStorageHandler<T> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
    private final        String           fileName;
    private final        String           fileExtension;
    private final        File             dataFolder;

    public FileStorageHandler(String fileName, String fileExtension, File dataFolder) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.dataFolder = dataFolder;
    }

    public static <T> FileStorageHandler<T> build(
        String fileName, String fileExtension, File dataFolder,
        LoadFile<T> loadFile,
        SaveFile<T> saveFile) {
        return new FileStorageHandler<>(fileName, fileExtension, dataFolder) {

            @Override
            protected T readFromFile(Path path) {
                return loadFile.loadFrom(path);
            }

            @Override
            protected void saveToFile(Path path, T t) {
                saveFile.saveTo(path, t);
            }
        };
    }

    protected abstract T readFromFile(Path path);

    protected abstract void saveToFile(Path path, T t);

    private File resolveFile() {
        return new File(this.dataFolder, this.fileName + this.fileExtension);
    }

    public Optional<T> load() {
        File file = resolveFile();
        return file.exists()
            ? Optional.ofNullable(readFromFile(file.toPath()))
            : Optional.empty();
    }

    public void save(T data) throws IOException {
        if (!this.dataFolder.mkdirs()) {
            return;
        }

        File file = resolveFile();
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();
        saveToFile(file.toPath(), data);
    }

    public void saveAndBackup(T data) throws IOException {
        if (!this.dataFolder.mkdirs()) {
            return;
        }

        File file = resolveFile();
        if (file.exists()) {
            File backupFile = new File(getBackupFolder(), getBackupFileName());
            try {
                Files.move(file.toPath(), backupFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file.createNewFile();
        saveToFile(file.toPath(), data);
    }

    protected File getBackupFolder() {
        File backupDirectory = new File(this.dataFolder, "backups");
        backupDirectory.mkdirs();
        return backupDirectory;
    }

    protected String getBackupFileName() {
        return this.fileName + "-" + DATE_FORMAT.format(Instant.now()) + this.fileExtension;
    }

    @FunctionalInterface
    public interface LoadFile<T> {

        T loadFrom(Path path);
    }

    @FunctionalInterface
    public interface SaveFile<T> {

        void saveTo(Path path, T t);
    }
}