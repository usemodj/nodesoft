package com.usemodj.nodesoft.web.rest.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class for handling pagination.
 *
 * <p>
 * Pagination uses the same principles as the <a href="https://developer.github.com/v3/#pagination">Github API</api>,
 * and follow <a href="http://tools.ietf.org/html/rfc5988">RFC 5988 (Link header)</a>.
 * </p>
 */
public class PaginationUtil {

    public static final int DEFAULT_OFFSET = 1;

    public static final int MIN_OFFSET = 1;

    public static final int DEFAULT_LIMIT = 20;

    public static final int MAX_LIMIT = 100;

    public static Pageable generatePageRequest(Integer offset, Integer limit) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        return new PageRequest(offset - 1, limit);
    }

    /*
     * generatePageRequest(1, 20, Direction.DESC, "lastName", "salary")
     */
    public static Pageable generatePageRequest(Integer offset, Integer limit, Direction direction, String... properties) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        //new PageRequest(0, 20, Direction.ASC, "lastName", "salary");
        return new PageRequest(offset - 1, limit, direction, properties);
    }

    /*
     * generatePageRequest(1, 20, new Sort(new Order(Direction.ASC, "lastName"),new Order(Direction.DESC, "salary"));
     */
    public static Pageable generatePageRequest(Integer offset, Integer limit, Sort sort) {
        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        /*
        new PageRequest(
        	0, 20, new Sort(
        	    new Order(Direction.ASC, "lastName"), 
        	    new Order(Direction.DESC, "salary")
        	  )
        	);
        	*/
        return new PageRequest(offset - 1, limit, sort);
    }

    public static HttpHeaders generatePaginationHttpHeaders(Page page, String baseUrl, Integer offset, Integer limit)
        throws URISyntaxException {

        if (offset == null || offset < MIN_OFFSET) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", "" + page.getTotalElements());
        String link = "";
        if (offset < page.getTotalPages()) {
            link = "<" + (new URI(baseUrl +"?page=" + (offset + 1) + "&per_page=" + limit)).toString()
                + ">; rel=\"next\",";
        }
        if (offset > 1) {
            link += "<" + (new URI(baseUrl +"?page=" + (offset - 1) + "&per_page=" + limit)).toString()
                + ">; rel=\"prev\",";
        }
        link += "<" + (new URI(baseUrl +"?page=" + page.getTotalPages() + "&per_page=" + limit)).toString()
            + ">; rel=\"last\"," +
            "<" + (new URI(baseUrl +"?page=" + 1 + "&per_page=" + limit)).toString()
            + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }
    public static HttpHeaders generatePaginationHttpHeaders(Page page, String baseUrl, Integer offset, Integer limit, String queryString)
            throws URISyntaxException {

            if (offset == null || offset < MIN_OFFSET) {
                offset = DEFAULT_OFFSET;
            }
            if (limit == null || limit > MAX_LIMIT) {
                limit = DEFAULT_LIMIT;
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", "" + page.getTotalElements());
            String link = "";
            if (offset < page.getTotalPages()) {
                link = "<" + (new URI(baseUrl +"?page=" + (offset + 1) + "&per_page=" + limit + (queryString != null? "&"+queryString: ""))).toString()
                    + ">; rel=\"next\",";
            }
            if (offset > 1) {
                link += "<" + (new URI(baseUrl +"?page=" + (offset - 1) + "&per_page=" + limit + (queryString != null? "&"+queryString: ""))).toString()
                    + ">; rel=\"prev\",";
            }
            link += "<" + (new URI(baseUrl +"?page=" + page.getTotalPages() + "&per_page=" + limit + (queryString != null? "&"+queryString: ""))).toString()
                + ">; rel=\"last\"," +
                "<" + (new URI(baseUrl +"?page=" + 1 + "&per_page=" + limit + (queryString != null? "&"+queryString: ""))).toString()
                + ">; rel=\"first\"";
            headers.add(HttpHeaders.LINK, link);
            return headers;
        }
}
