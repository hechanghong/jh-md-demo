package com.michael.web.rest;

import com.michael.BlogApp;

import com.michael.domain.Quote;
import com.michael.repository.QuoteRepository;
import com.michael.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the QuoteResource REST controller.
 *
 * @see QuoteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BlogApp.class)
public class QuoteResourceIntTest {

    private static final Long DEFAULT_QUOTE_SEQ_NO = 1L;
    private static final Long UPDATED_QUOTE_SEQ_NO = 2L;

    private static final String DEFAULT_PRODUCT_ID = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LINE = "AAAAAAAAAA";
    private static final String UPDATED_LINE = "BBBBBBBBBB";

    private static final Double DEFAULT_PREMIUM = 1D;
    private static final Double UPDATED_PREMIUM = 2D;

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restQuoteMockMvc;

    private Quote quote;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QuoteResource quoteResource = new QuoteResource(quoteRepository);
        this.restQuoteMockMvc = MockMvcBuilders.standaloneSetup(quoteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Quote createEntity(EntityManager em) {
        Quote quote = new Quote()
            .quoteSeqNo(DEFAULT_QUOTE_SEQ_NO)
            .productId(DEFAULT_PRODUCT_ID)
            .productName(DEFAULT_PRODUCT_NAME)
            .line(DEFAULT_LINE)
            .premium(DEFAULT_PREMIUM);
        return quote;
    }

    @Before
    public void initTest() {
        quote = createEntity(em);
    }

    @Test
    @Transactional
    public void createQuote() throws Exception {
        int databaseSizeBeforeCreate = quoteRepository.findAll().size();

        // Create the Quote
        restQuoteMockMvc.perform(post("/api/quotes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quote)))
            .andExpect(status().isCreated());

        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeCreate + 1);
        Quote testQuote = quoteList.get(quoteList.size() - 1);
        assertThat(testQuote.getQuoteSeqNo()).isEqualTo(DEFAULT_QUOTE_SEQ_NO);
        assertThat(testQuote.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testQuote.getProductName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(testQuote.getLine()).isEqualTo(DEFAULT_LINE);
        assertThat(testQuote.getPremium()).isEqualTo(DEFAULT_PREMIUM);
    }

    @Test
    @Transactional
    public void createQuoteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = quoteRepository.findAll().size();

        // Create the Quote with an existing ID
        quote.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuoteMockMvc.perform(post("/api/quotes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quote)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllQuotes() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);

        // Get all the quoteList
        restQuoteMockMvc.perform(get("/api/quotes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quote.getId().intValue())))
            .andExpect(jsonPath("$.[*].quoteSeqNo").value(hasItem(DEFAULT_QUOTE_SEQ_NO.intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME.toString())))
            .andExpect(jsonPath("$.[*].line").value(hasItem(DEFAULT_LINE.toString())))
            .andExpect(jsonPath("$.[*].premium").value(hasItem(DEFAULT_PREMIUM.doubleValue())));
    }

    @Test
    @Transactional
    public void getQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);

        // Get the quote
        restQuoteMockMvc.perform(get("/api/quotes/{id}", quote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(quote.getId().intValue()))
            .andExpect(jsonPath("$.quoteSeqNo").value(DEFAULT_QUOTE_SEQ_NO.intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.toString()))
            .andExpect(jsonPath("$.productName").value(DEFAULT_PRODUCT_NAME.toString()))
            .andExpect(jsonPath("$.line").value(DEFAULT_LINE.toString()))
            .andExpect(jsonPath("$.premium").value(DEFAULT_PREMIUM.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingQuote() throws Exception {
        // Get the quote
        restQuoteMockMvc.perform(get("/api/quotes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        int databaseSizeBeforeUpdate = quoteRepository.findAll().size();

        // Update the quote
        Quote updatedQuote = quoteRepository.findOne(quote.getId());
        updatedQuote
            .quoteSeqNo(UPDATED_QUOTE_SEQ_NO)
            .productId(UPDATED_PRODUCT_ID)
            .productName(UPDATED_PRODUCT_NAME)
            .line(UPDATED_LINE)
            .premium(UPDATED_PREMIUM);

        restQuoteMockMvc.perform(put("/api/quotes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedQuote)))
            .andExpect(status().isOk());

        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeUpdate);
        Quote testQuote = quoteList.get(quoteList.size() - 1);
        assertThat(testQuote.getQuoteSeqNo()).isEqualTo(UPDATED_QUOTE_SEQ_NO);
        assertThat(testQuote.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testQuote.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testQuote.getLine()).isEqualTo(UPDATED_LINE);
        assertThat(testQuote.getPremium()).isEqualTo(UPDATED_PREMIUM);
    }

    @Test
    @Transactional
    public void updateNonExistingQuote() throws Exception {
        int databaseSizeBeforeUpdate = quoteRepository.findAll().size();

        // Create the Quote

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restQuoteMockMvc.perform(put("/api/quotes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(quote)))
            .andExpect(status().isCreated());

        // Validate the Quote in the database
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteQuote() throws Exception {
        // Initialize the database
        quoteRepository.saveAndFlush(quote);
        int databaseSizeBeforeDelete = quoteRepository.findAll().size();

        // Get the quote
        restQuoteMockMvc.perform(delete("/api/quotes/{id}", quote.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Quote> quoteList = quoteRepository.findAll();
        assertThat(quoteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Quote.class);
    }
}
