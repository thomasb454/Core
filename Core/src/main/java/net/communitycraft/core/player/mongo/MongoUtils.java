package net.communitycraft.core.player.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import lombok.NonNull;
import net.communitycraft.core.player.CPermissible;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MongoUtils {
    public static BasicDBObjectBuilder getObjectForPermissible(CPermissible permissible) {
        BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
        if (permissible.getTablistColor() != null) builder.add(MongoKey.GROUPS_TABLIST_COLOR_KEY.toString(), permissible.getTablistColor().name());
        if (permissible.getChatColor() != null) builder.add(MongoKey.GROUPS_CHAT_COLOR_KEY.toString(), permissible.getChatColor().name());
        if (permissible.getChatPrefix() != null) builder.add(MongoKey.GROUPS_CHAT_PREFIX_KEY.toString(), permissible.getChatPrefix());
        if (permissible.getDeclaredPermissions().size() > 0) builder.add(MongoKey.GROUPS_PERMISSIONS_KEY.toString(), getDBObjectFor(permissible.getDeclaredPermissions()));
        return builder;
    }

    public static void combineObjectBuilders(BasicDBObjectBuilder h, BasicDBObjectBuilder k) {
        DBObject kObject = k.get();
        for (String s : kObject.keySet()) {
            h.add(s, kObject.get(s));
        }
    }

    public static CPermissible getPermissibileDataFor(DBObject object) {
        final Map<String, Boolean> declaredPermissions = getMapFor(getValueFrom(object, MongoKey.GROUPS_PERMISSIONS_KEY, BasicDBObject.class), Boolean.class);
        String cColor = getValueFrom(object, MongoKey.GROUPS_CHAT_COLOR_KEY, String.class); final ChatColor chatColor = cColor == null ? null : ChatColor.valueOf(cColor);
        String tColor = getValueFrom(object, MongoKey.GROUPS_TABLIST_COLOR_KEY, String.class); final ChatColor tablistColor = tColor == null ? null : ChatColor.valueOf(tColor);
        final String chatPrefix = getValueFrom(object, MongoKey.GROUPS_CHAT_PREFIX_KEY, String.class);
        return new CPermissible() {
            @Override
            public ChatColor getChatColor() {
                return chatColor;
            }

            @Override
            public ChatColor getTablistColor() {
                return tablistColor;
            }

            @Override
            public String getChatPrefix() {
                return chatPrefix;
            }

            @Override
            public void setChatColor(ChatColor color) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public void setTablistColor(ChatColor color) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public void setChatPrefix(String prefix) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public void setPermission(String permission, Boolean value) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public void unsetPermission(String permission) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public boolean hasPermission(String permission) {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public Map<String, Boolean> getDeclaredPermissions() {
                return declaredPermissions;
            }

            @Override
            public void reloadPermissions() {
                throw new UnsupportedOperationException("This CPermissible is for data access only!");
            }

            @Override
            public String getName() {
                throw new UnsupportedOperationException("This data is currently not available!");
            }
        };
    }
    public static <T> T getValueFrom(DBObject object, @NonNull Object key, Class<T> clazz) {
        return getValueFrom(object, key.toString(), clazz);
    }

    @SuppressWarnings("UnusedParameters")
    public static <T> T getValueFrom(DBObject object, @NonNull String key, Class<T> clazz) {
        if (object == null) return null;
        try {
            //noinspection unchecked
            return (T) (object.get(key));
        } catch (ClassCastException ex) {
            return null;
        }
    }

    @SuppressWarnings("UnusedParameters")
    public static <T> List<T> getListFor(BasicDBList list, Class<T> clazz) {
        List<T> tList = new ArrayList<>();
        if (list == null) return null;
        for (Object o : list) {
            try {
                //noinspection unchecked
                tList.add((T)applyTypeFiltersForObject(o));
            } catch (ClassCastException ignored) {}
        }
        return tList;
    }

    public static BasicDBList getDBListFor(List<?> list) {
        BasicDBList dbList = new BasicDBList();
        if (list == null) return null;
        for (Object o : list) {
            dbList.add(applyTypeFiltersForDB(o));
        }
        return dbList;
    }

    public static DBObject getDBObjectFor(Map<?,?> map) {
        BasicDBObject basicDBObject = new BasicDBObject();
        if (map == null) return null;
        for (Map.Entry<?, ?> stringEntry : map.entrySet()) {
            basicDBObject.put(stringEntry.getKey().toString(), applyTypeFiltersForDB(stringEntry.getValue()));
        }
        return basicDBObject;
    }

    public static Object applyTypeFiltersForDB(Object i) {
        Object value = i;
        if (value instanceof List && !(i instanceof BasicDBList)) value = getDBListFor((List<?>) value);
        else if (value instanceof Map && !(i instanceof DBObject)) value = getDBObjectFor((Map) value);
        return value;
    }

    public static Object applyTypeFiltersForObject(Object i) {
        Object value = i;
        if (i instanceof BasicDBList) value = getListFor((BasicDBList) i, Object.class);
        else if (i instanceof DBObject) value = getMapFor((DBObject) i);
        return value;
    }

    public static Map<String, Object> getMapFor(DBObject object) {
        return getMapFor(object, Object.class);
    }

    @SuppressWarnings("UnusedParameters")
    public static <T> Map<String, T> getMapFor(DBObject object, Class<T> valueType) {
        HashMap<String, T> map = new HashMap<>();
        if (object == null) return null;
        for (String s : object.keySet()) {
            T t;
            try {
                //noinspection unchecked
                t = (T) applyTypeFiltersForObject(object.get(s));
            } catch (ClassCastException e) {
                continue;
            }
            map.put(s, t);
        }
        return map;
    }

}