package me.kubbidev.nexuspowered.reflect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsulates a snapshot version of Minecraft.
 *
 * @author Kristian (ProtocolLib)
 */
public class SnapshotVersion implements Comparable<SnapshotVersion> {

    public static final Comparator<SnapshotVersion> COMPARATOR = Comparator.nullsFirst(Comparator
        .comparing(SnapshotVersion::getSnapshotDate)
        .thenComparing(SnapshotVersion::getSnapshotWeekVersion)
    );

    private static final Pattern SNAPSHOT_PATTERN = Pattern.compile("(\\d{2}w\\d{2})([a-z])");
    private final        Date    snapshotDate;
    private final        int     snapshotWeekVersion;
    private transient    String  rawString;

    private SnapshotVersion(String version) {
        Matcher matcher = SNAPSHOT_PATTERN.matcher(version.trim());
        if (matcher.matches()) {
            try {
                this.snapshotDate = getDateFormat().parse(matcher.group(1));
                this.snapshotWeekVersion = matcher.group(2).charAt(0) - 'a';
                this.rawString = version;
            } catch (ParseException e) {
                throw new IllegalArgumentException("Date implied by snapshot version is invalid.", e);
            }
        } else {
            throw new IllegalArgumentException("Cannot parse " + version + " as a snapshot version.");
        }
    }

    /**
     * Parses a snapshot version
     *
     * @param version the version string
     * @return the parsed version
     * @throws IllegalArgumentException if the version is not a snapshot version
     */
    @NotNull
    public static SnapshotVersion parse(String version) throws IllegalArgumentException {
        return new SnapshotVersion(version);
    }

    /**
     * Retrieve the snapshot date parser.
     * <p>
     * We have to create a new instance of SimpleDateFormat every time as it is not thread safe.
     *
     * @return The date formatter.
     */
    private static SimpleDateFormat getDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yy'w'ww", Locale.US);
        format.setLenient(false);
        return format;
    }

    /**
     * Retrieve the snapshot version within a week, starting at zero.
     *
     * @return The weekly version
     */
    public int getSnapshotWeekVersion() {
        return this.snapshotWeekVersion;
    }

    /**
     * Retrieve the week this snapshot was released.
     *
     * @return The week.
     */
    public Date getSnapshotDate() {
        return this.snapshotDate;
    }

    /**
     * Retrieve the raw snapshot string (yy'w'ww[a-z]).
     *
     * @return The snapshot string.
     */
    public String getSnapshotString() {
        if (this.rawString == null) {
            // It's essential that we use the same locale
            Calendar current = Calendar.getInstance(Locale.US);
            current.setTime(this.snapshotDate);
            this.rawString = String.format("%02dw%02d%s",
                current.get(Calendar.YEAR) % 100,
                current.get(Calendar.WEEK_OF_YEAR),
                (char) ('a' + this.snapshotWeekVersion));
        }
        return this.rawString;
    }

    @Override
    public int compareTo(@NotNull SnapshotVersion that) {
        return COMPARATOR.compare(this, that);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof SnapshotVersion other)) {
            return false;
        }
        return Objects.equals(this.snapshotDate, other.getSnapshotDate()) &&
            this.snapshotWeekVersion == other.getSnapshotWeekVersion();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.snapshotDate, this.snapshotWeekVersion);
    }

    @Override
    public String toString() {
        return getSnapshotString();
    }
}