/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.mapper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import org.jdbi.v3.core.config.ConfigRegistry;

import static org.jdbi.v3.core.generic.GenericTypes.getErasedType;

/**
 * Column mapper factory which knows how to map JavaTime objects:
 * <ul>
 *     <li>{@link Instant}</li>
 *     <li>{@link LocalDate}</li>
 *     <li>{@link LocalTime}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link OffsetDateTime}</li>
 *     <li>{@link ZonedDateTime}</li>
 *     <li>{@link ZoneId}</li>
 * </ul>
 */
class JavaTimeMapperFactory implements ColumnMapperFactory {
    private final Map<Class<?>, ColumnMapper<?>> mappers = new IdentityHashMap<>();

    JavaTimeMapperFactory() {
        mappers.put(Instant.class, new GetterMapper<>(JavaTimeMapperFactory::getInstant));
        mappers.put(LocalDate.class, new GetterMapper<>(JavaTimeMapperFactory::getLocalDate));
        mappers.put(LocalTime.class, new GetterMapper<>(JavaTimeMapperFactory::getLocalTime));
        mappers.put(OffsetTime.class, new GetterMapper<>(JavaTimeMapperFactory::getOffsetTime));
        mappers.put(LocalDateTime.class, new GetterMapper<>(JavaTimeMapperFactory::getLocalDateTime));
        mappers.put(OffsetDateTime.class, new GetterMapper<>(JavaTimeMapperFactory::getOffsetDateTime));
        mappers.put(ZonedDateTime.class, new GetterMapper<>(JavaTimeMapperFactory::getZonedDateTime));
        mappers.put(ZoneId.class, new GetterMapper<>(JavaTimeMapperFactory::getZoneId));
        mappers.put(ZoneOffset.class, new GetterMapper<>(JavaTimeMapperFactory::getZoneOffset));
        mappers.put(Year.class, new GetterMapper<>(JavaTimeMapperFactory::getYear));
        mappers.put(YearMonth.class, new GetterMapper<>(JavaTimeMapperFactory::getYearMonth));
    }

    @Override
    public Optional<ColumnMapper<?>> build(Type type, ConfigRegistry config) {
        Class<?> rawType = getErasedType(type);

        return Optional.ofNullable(mappers.get(rawType));
    }

    private static Instant getInstant(ResultSet r, int i) throws SQLException {
        Timestamp ts = r.getTimestamp(i);
        return ts == null ? null : ts.toInstant();
    }

    private static LocalDate getLocalDate(ResultSet r, int i) throws SQLException {
        Timestamp ts = r.getTimestamp(i);
        return ts == null ? null : ts.toLocalDateTime().toLocalDate();
    }

    private static LocalDateTime getLocalDateTime(ResultSet r, int i) throws SQLException {
        Timestamp ts = r.getTimestamp(i);
        return ts == null ? null : ts.toLocalDateTime();
    }

    private static OffsetDateTime getOffsetDateTime(ResultSet r, int i) throws SQLException {
        Timestamp ts = r.getTimestamp(i);
        return ts == null ? null : OffsetDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
    }

    private static ZonedDateTime getZonedDateTime(ResultSet r, int i) throws SQLException {
        Timestamp ts = r.getTimestamp(i);
        return ts == null ? null : ZonedDateTime.ofInstant(ts.toInstant(), ZoneId.systemDefault());
    }

    private static LocalTime getLocalTime(ResultSet r, int i) throws SQLException {
        Time time = r.getTime(i);
        return time == null ? null : time.toLocalTime();
    }

    private static OffsetTime getOffsetTime(ResultSet r, int i) throws SQLException {
        Timestamp timestamp = r.getTimestamp(i);
        return timestamp == null ? null : OffsetTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
    }

    private static ZoneId getZoneId(ResultSet r, int i) throws SQLException {
        String id = r.getString(i);
        return id == null ? null : ZoneId.of(id);
    }

    private static ZoneOffset getZoneOffset(ResultSet r, int i) throws SQLException {
        String offset = r.getString(i);
        return offset == null ? null : ZoneOffset.of(offset);
    }

    private static Year getYear(ResultSet r, int i) throws SQLException {
        int year = r.getInt(i);
        return r.wasNull() ? null : Year.of(year);
    }

    private static YearMonth getYearMonth(ResultSet r, int i) throws SQLException {
        int yearMonth = r.getInt(i);
        if (r.wasNull()) {
            return null;
        }
        int year = yearMonth / 100;
        return YearMonth.of(year, Month.of(yearMonth - year * 100));
    }
}
