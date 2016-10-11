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
package org.jdbi.v3.core;

import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class TestIterator
{
    @Rule
    public H2DatabaseRule db = new H2DatabaseRule();

    private Handle h;

    @Before
    public void setUp() throws Exception {
        h = db.openHandle();
    }

    @After
    public void doTearDown() throws Exception {
        assertThat(h.isClosed()).isTrue().withFailMessage("Handle was not closed correctly!");
    }

    @Test
    public void testSimple() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();
        h.createStatement("insert into something (id, name) values (3, 'john')").execute();

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    public void testEmptyWorksToo() throws Exception {
        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        assertThat(it.hasNext()).isFalse();
    }

    @Test
    public void testHasNext() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();
        h.createStatement("insert into something (id, name) values (3, 'john')").execute();

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        assertThat(it.hasNext()).isTrue();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isFalse();
        assertThat(it.hasNext()).isFalse();
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    public void testNext() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();
        h.createStatement("insert into something (id, name) values (3, 'john')").execute();

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        assertThat(it.hasNext()).isTrue();
        it.next();
        it.next();
        it.next();
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    public void testJustNext() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();
        h.createStatement("insert into something (id, name) values (3, 'john')").execute();

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        it.next();
        it.next();
        it.next();
    }

    @Test
    public void testTwoTwo() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();
        h.createStatement("insert into something (id, name) values (3, 'john')").execute();

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        it.next();
        it.next();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isFalse();
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    public void testTwoOne() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();
        h.createStatement("insert into something (id, name) values (3, 'john')").execute();

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        assertThat(it.hasNext()).isTrue();
        it.next();
        it.next();
        assertThat(it.hasNext()).isTrue();
        it.next();
        assertThat(it.hasNext()).isFalse();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplodeIterator() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();
        h.createStatement("insert into something (id, name) values (3, 'john')").execute();

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        try {
            assertThat(it.hasNext()).isTrue();
            it.next();
            assertThat(it.hasNext()).isTrue();
            it.next();
            assertThat(it.hasNext()).isTrue();
            it.next();
            assertThat(it.hasNext()).isFalse();
        }
        catch (Throwable t) {
            fail("unexpected throwable:" + t.getMessage());
        }

        it.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyExplosion() throws Exception {

        ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator();

        it.next();
    }

    @Test
    public void testNonPathologicalJustNext() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();

        // Yes, you *should* use first(). But sometimes, an iterator is passed 17 levels deep and then
        // used in this way (Hello Jackson!).
        final Map<String, Object> result = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator()
            .next();

        assertThat(result.get("id")).isEqualTo(1L);
        assertThat(result.get("name")).isEqualTo("eric");
    }

    @Test
    public void testStillLeakingJustNext() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();

        // Yes, you *should* use first(). But sometimes, an iterator is passed 17 levels deep and then
        // used in this way (Hello Jackson!).
        final Map<String, Object> result = h.createQuery("select * from something order by id")
            .cleanupHandle()
            .iterator()
            .next();

        assertThat(result.get("id")).isEqualTo(1L);
        assertThat(result.get("name")).isEqualTo("eric");

        assertThat(h.isClosed()).isFalse();

        // The Query created by createQuery() above just leaked a Statement and a ResultSet. It is necessary
        // to explicitly close the iterator in that case. However, as this test case is using the CachingStatementBuilder,
        // closing the handle will close the statements (which also closes the result sets).
        //
        // Don't try this at home folks. It is still very possible to leak stuff with the iterators.

        h.close();
    }

    @Test
    public void testLessLeakingJustNext() throws Exception {
        h.createStatement("insert into something (id, name) values (1, 'eric')").execute();
        h.createStatement("insert into something (id, name) values (2, 'brian')").execute();

        try (final ResultIterator<Map<String, Object>> it = h.createQuery("select * from something order by id")
                .cleanupHandle()
                .iterator()) {
            final Map<String, Object> result =  it.next();
            assertThat(result).containsEntry("id", 1L).containsEntry("name", "eric");

            assertThat(h.isClosed()).isFalse();
        }
    }
}