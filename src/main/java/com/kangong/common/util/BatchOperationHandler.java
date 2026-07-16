package com.kangong.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

public class BatchOperationHandler {

	private static final int DEFAULT_THREAD_POOL_SIZE = 20;
	private static final int DEFAULT_MAX_RETRIES = 2;
	private static final long RETRY_BASE_DELAY_MS = 2000;

	@FunctionalInterface
	public interface ThrowingConsumer<T> {
		void accept(T item) throws Exception;
	}

	private static <T> boolean executeWithRetry(T item, ThrowingConsumer<T> processor,
			Function<T, String> failedUrlMapper, String operationName, Logger log, int maxRetries) {
		for (int attempt = 0; attempt <= maxRetries; attempt++) {
			try {
				long itemStart = System.currentTimeMillis();
				processor.accept(item);
				long itemElapsed = System.currentTimeMillis() - itemStart;
				if (itemElapsed > 3000) {
					log.warn("[TIME] {} 단건 느림: {}ms - {}", operationName, itemElapsed, failedUrlMapper.apply(item));
				}
				if (attempt > 0) {
					log.info("{} 재시도 성공 ({}회차): {}", operationName, attempt + 1, failedUrlMapper.apply(item));
				}
				return true;
			} catch (Exception e) {
				String failedUrl = failedUrlMapper.apply(item);
				if (attempt < maxRetries) {
					long delay = RETRY_BASE_DELAY_MS * (1L << attempt);
					log.warn("{} 실패, {}ms 후 재시도 ({}/{}): {} - {}", operationName, delay, attempt + 1, maxRetries, failedUrl, e.getMessage());
					try { Thread.sleep(delay); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return false; }
				} else {
					log.error("{} 최종 실패 ({}회 재시도 후): {} - {}", operationName, maxRetries, failedUrl, e.getMessage());
				}
			}
		}
		return false;
	}

	public static <T> void executeBatch(
			List<T> items,
			ThrowingConsumer<T> processor,
			Function<T, String> failedUrlMapper,
			String operationName,
			Logger log) {
		long batchStart = System.currentTimeMillis();
		List<String> failedUrlList = new ArrayList<>();
		int processedCount = 0;
		for (T item : items) {
			if (executeWithRetry(item, processor, failedUrlMapper, operationName, log, DEFAULT_MAX_RETRIES)) {
				processedCount++;
			} else {
				failedUrlList.add(failedUrlMapper.apply(item));
			}
		}
		logBatchResult(operationName, batchStart, processedCount, items.size(), failedUrlList, 1, log);
	}

	public static <T> void executeBatchParallel(
			List<T> items,
			ThrowingConsumer<T> processor,
			Function<T, String> failedUrlMapper,
			String operationName,
			Logger log) {
		executeBatchParallel(items, processor, failedUrlMapper, operationName, log, DEFAULT_THREAD_POOL_SIZE);
	}

	public static <T> void executeBatchParallel(
			List<T> items,
			ThrowingConsumer<T> processor,
			Function<T, String> failedUrlMapper,
			String operationName,
			Logger log,
			int threadPoolSize) {
		long batchStart = System.currentTimeMillis();
		List<String> failedUrlList = Collections.synchronizedList(new ArrayList<>());
		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

		try {
			List<Future<?>> futures = new ArrayList<>();
			for (T item : items) {
				futures.add(executor.submit(() -> {
					if (!executeWithRetry(item, processor, failedUrlMapper, operationName, log, DEFAULT_MAX_RETRIES)) {
						failedUrlList.add(failedUrlMapper.apply(item));
					}
				}));
			}
			for (Future<?> future : futures) {
				future.get();
			}
		} catch (Exception e) {
			log.error("{} 병렬 처리 중 오류: {}", operationName, e.getMessage());
		} finally {
			executor.shutdown();
		}

		int processedCount = items.size() - failedUrlList.size();
		logBatchResult(operationName, batchStart, processedCount, items.size(), failedUrlList, threadPoolSize, log);
	}

	private static void logBatchResult(String operationName, long batchStart, int processedCount,
			int totalCount, List<String> failedUrlList, int threadPoolSize, Logger log) {
		long batchElapsed = System.currentTimeMillis() - batchStart;
		String mode = threadPoolSize > 1 ? String.format("병렬완료(%d스레드)", threadPoolSize) : "완료";
		log.info("[TIME] {} {}: 총 {}ms, 처리 {}/{}, 실패 {}, 평균 {}ms/건",
				operationName, mode, batchElapsed, processedCount, totalCount,
				failedUrlList.size(), processedCount > 0 ? batchElapsed / processedCount : 0);
		if (!failedUrlList.isEmpty()) {
			log.error("=== {} 최종 실패 URL 목록 ({}/{}) ===", operationName, failedUrlList.size(), totalCount);
			for (String url : failedUrlList) {
				log.error("  실패: {}", url);
			}
		}
	}
}
