This sample focuses on time-zone handling. It shows how the dateline can follow one specific zone while individual rows use their own zones, which is especially relevant for global scheduling scenarios.

```java
public class ZoneIdRow extends Row<ZoneIdRow, ZoneIdRow, Activity> {

    public ZoneIdRow(ZoneId zoneId) {
        super(zoneId.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()));
        setZoneId(zoneId);
    }
}
```
