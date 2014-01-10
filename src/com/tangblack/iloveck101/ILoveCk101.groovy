package com.tangblack.iloveck101

import groovy.util.logging.Log

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.jsoup.Jsoup
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Log
class ILoveCk101
{
    private static final String BASE_URL = 'http://ck101.com/'
	private ExecutorService threadPool = Executors.newFixedThreadPool(8)

    /**
     * Determine the url is valid. And check if the url contains any thread link or it's a thread.
     * 
     * @param url
     * @see <a href="http://groovy.codehaus.org/Regular+Expressions">Regular Expressions</a>
     * @see <a href="http://blog.csdn.net/bincavin/article/details/8166659">ExecutorService java線程池主線程等待子線程執行完成</a>
     */
    void run(url)
    {
        log.info("run() url=$url")
        
        if ((url =~ 'ck101.com') == false)
        {
            log.warning("$url is not ck101 url!")
            return
        }
        
        if ((url =~ 'thread'))
        {
            retriveThread(url)
        }
        else
        {
            def threadList = retriveThreadList(url)
			for (String threadUrl : threadList)
			{
				log.info("threadUrl=$threadUrl")
				retriveThread(threadUrl) 
			}
        }
		
		
		/* Invoke shutdown() after submit all tasks. */
		threadPool.shutdown()
		log.info("threadPool.shutdown()")
		
		/* Main thread waits until finishing download tasks. */
		while (!threadPool.isTerminated())
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
	   }
		
    }
	
    /**
     * download images from given ck101 URL.
     * 
     * @param url
     * 
     * @see <a href="http://stackoverflow.com/questions/754307/regex-to-replace-characters-that-windows-doesnt-accept-in-a-filename">sRegex to replace characters that Windows doesn't accept in a filename</a>
     */
    private void retriveThread(String url)
    {
        log.info("retriveThread() url=$url")
        
		/* check if the url has http prefix. */
		if (url.startsWith('http') == false)
		{	
			url = 'http' + url
		}

        Document document = parseUrl(url)
		if (document == null)
		{
			log.warning('Oops, can not fetch the page!')
			System.exit(0)
		}
		
				
		/* find thread id. */
		def matcher = (url =~ /thread-(\d+)-.*/)
		if (matcher == false)
		{
			return
		}
		def threadId = matcher[0][1]
		log.info("threadId=$threadId")
		
		
		
        /* create `iloveck101` folder in ~/Pictures */
        log.fine(System.getProperty("user.home"))
        String desktopPath = System.getProperty("user.home") + File.separator + 'Desktop'
        File baseFolder = new File(desktopPath, 'ILoveCk101')
		if (baseFolder.exists() == false)
		{
			baseFolder.mkdir()
		}
		
		/* create target folder for saving images. */
		String title = document.title()
		log.info("title=$title")
		title.replaceAll("[\\/:*?\"<>|]", "") // Remove invalid string in windows.
		File folder = new File(baseFolder, "$threadId - $title")
		if (folder.exists() == false)
		{
			folder.mkdir()
		}
		
		
		/* Save images. */
		Elements imgages = document.select('img[file]')
		for (org.jsoup.nodes.Element img : imgages)
		{
			String imgUrl = img.attr("file")
			log.info("imgUrl=$imgUrl")
			
			/* ignore useless image. */
			if (imgUrl.startsWith('http') == false)
			{
				log.info("$imgUrl imgUrl.startsWith('http') == false")
				continue
			}
			
			threadPool.submit({
				log.info("threadPool.submit()")
				downloadUrlClosure(imgUrl, folder)
			})
		}
		
		//
		//    for chunked_image_urls in chunked(image_urls, CHUNK_SIZE):
		//        jobs = [gevent.spawn(process_image_worker, image_url)
		//                for image_url in chunked_image_urls]
		//        gevent.joinall(jobs)
		
		//    def process_image_worker(image_url):
		//        filename = image_url.rsplit('/', 1)[1]
		//
		//        # ignore useless image
		//        if not image_url.startswith('http'):
		//            return
		//
		//        # fetch image
		//        print 'Fetching %s ...' % image_url
		//        resp = requests.get(image_url, headers=REQUEST_HEADERS)
		//
		//        # ignore small images
		//        content_type, width, height = get_image_info(resp.content)
		//        if width < 400 or height < 400:
		//            print "image is too small"
		//            return
		//
		//        # save image
		//        with open(os.path.join(folder, filename), 'wb+') as f:
		//            f.write(resp.content)
    }
	
	/**
	 * @see <a href="http://stackoverflow.com/questions/4674995/groovy-download-image-from-url">Groovy download image from url</a>
	 * @see <a href="http://groovy.codehaus.org/Concurrency+with+Groovy">Concurrency with Groovy</a>
	 */
	private def downloadUrlClosure = { imgUrl, folder ->
			
			/* fetch image. */
			
			/* ignore small images. */
			
			log.info("Downloading $imgUrl ...")
			new File(folder, "${imgUrl.tokenize('/')[-1]}.png").withOutputStream { out ->
				out << new URL(imgUrl).openStream()
			}
			
			/* Print thread id. */
//			println Thread.allStackTraces.keySet().join('\n')
	}
	
    /**
     * The url may contains many thread links. We parse them out.
     * 
     * @param url
     */
    private List<String> retriveThreadList(url)
    {
        log.info("retriveThreadList() url=$url")
        
		def urlList = []
        def urlMap = [:]
		
		Document document = parseUrl(url)
		if (document == null)
		{
			log.warning('Oops, can not fetch the page!')
			System.exit(0)
		}
		
		Elements links = document.select("a[href]"); // a with href
		log.info("links.size()=${links.size()}")
		for (Element link : links)
		{
			def href = link.attr('href')
			log.info("href=$href")
			
			/* check if the url has http prefix. */
			if (href.startsWith('http') == false)
			{
				href = BASE_URL + href
			}
			
			/* Use map to remove duplicate links. */
			if ((href =~ 'thread'))
			{
                urlMap[href] = href
			}

		}
        
        urlList = new ArrayList(urlMap.keySet())
        log.info("urlList.size()=${urlList.size()}")
		return urlList
    }
	
	/**
	 * parse image_url from given url.
	 */
	private Document parseUrl(url)
	{
		log.info("parseUrl() url=$url")
		
		for (i in 1..10)
		{
			log.info("Try $i time...")
			
			try
			{
				/* fetch html. */
				Response response =
						Jsoup.connect(url)
						.header('Host', 'ck101.com')
						.header('Connection', 'keep-alive')
						.header('Cache-Control', 'max-age=0')
						.header('Accept', 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8')
						.header('Accept-Encoding', 'gzip,deflate,sdch')
						.header('Accept-Language', 'zh-TW,zh;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2')
						.userAgent('Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36')
						.timeout(60000)
						.execute()
				
				if (response.statusCode() != 200)
				{
					log.info("Status code is $response.statusCode(), retrying ...")
					continue
				}
				return response.parse();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				
				log.info('Retrying ...')
				continue
			}
		}
		
		return null
	}
	
	/**
     * Main.
     */
    static main(args)
    {    
        if (args)
        {
            String url = args[0]
            new ILoveCk101().run(url)
        }
        else
        {
            println "Please provide URL from ck101"
        }
    }
}
