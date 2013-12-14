package com.tangblack.iloveck101

import groovy.util.logging.Log

import org.jsoup.Jsoup
import org.jsoup.Connection.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Log
class ILoveCk101
{
    
    private static final String BASE_URL = 'http://ck101.com/'
    static def thread_pool = Executors.newFixedThreadPool(8)
    static def imageQueue = []
    def downloadImageJob = { folder, url->
        new File(folder, "${url.tokenize('/')[-1]}").withOutputStream { out ->
          out << new URL(url).openStream()
        }
    }
    /**
     * Determine the url is valid. And check if the url contains any thread link or it's a thread.
     * 
     * @param url
     * @see <a href="http://groovy.codehaus.org/Regular+Expressions">Regular Expressions</a>
     */
    void run(url)
    {
        // log.info("run() url=$url")
        
        if ((url =~ 'ck101.com') == false)
        {
            // log.warning("$url is not ck101 url!")
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
				// log.info("threadUrl=$threadUrl")
				retriveThread(threadUrl) //TODO
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
        // log.info("retriveThread() url=$url")
        
		/* check if the url has http prefix. */
		if (url.startsWith('http') == false)
		{	
			url = 'http' + url
		}

        Document document = parseUrl(url)
		if (document == null)
		{
			// log.warning('Oops, can not fetch the page!')
			System.exit(0)
		}
		
				
		/* find thread id. */
		def matcher = (url =~ /thread-(\d+)-.*/)
		if (matcher == false)
		{
			return
		}
		def threadId = matcher[0][1]
		// log.info("threadId=$threadId")
		
		
		
        /* create `iloveck101` folder in ~/Pictures */
        // log.fine(System.getProperty("user.home"))
        String desktopPath = System.getProperty("user.home") + File.separator + 'Desktop'
        File baseFolder = new File(desktopPath, 'ILoveCk101')
		if (baseFolder.exists() == false)
		{
			baseFolder.mkdir()
		}
		
		/* create target folder for saving images. */
		String title = document.title()
		// log.info("title=$title")
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
			// log.info(img.attr("file"))
			downloadImage(folder, img.attr("file"))
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
     * The url may contains many thread links. We parse them out.
     * 
     * @param url
     */
    private List<String> retriveThreadList(url)
    {
        // log.info("retriveThreadList() url=$url")
		def urlList
        def urlMap = [:]
		// List<String> urlList = []
		
		Document document = parseUrl(url)
		if (document == null)
		{
			log.warning('Oops, can not fetch the page!')
			System.exit(0)
		}
		
		Elements links = document.select("a[href]"); // a with href
		for (Element link : links)
		{
			def href = link.attr('href')
			// log.info("href=$href")
			
			/* check if the url has http prefix. */
			if (href.startsWith('http') == false)
			{
				href = BASE_URL + href
			}
			
			
			if ((href =~ 'thread'))
			{
                urlMap[href] = href
			}

		}
        urlList = new ArrayList(urlMap.keySet())
		println urlList.size()
		return urlList
    }
	
	/**
	 * parse image_url from given url.
	 */
	private Document parseUrl(url)
	{
		// log.info("parseUrl() url=$url")
		
		for (i in 1..3)
		{
			// log.info("Try $i time...")
			
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
	 * 
	 * @param folder
	 * @param url
	 * 
	 * @see <a href="http://stackoverflow.com/questions/4674995/groovy-download-image-from-url">Groovy download image from url</a>
	 */
	private void downloadImage(File folder, String url)
	{
		// log.info("downloadImage() folder=$folder, url=$url")
		
		/* ignore useless image. */
		if (url.startsWith('http') == false)
		{
			return
		}
		
		/* fetch image. */
		
		/* ignore small images. */
		
		// log.info("Downloading $url ...")
        // imageQueue << [folder: folder, url: url ]
        try {
            thread_pool.submit({
                downloadImageJob folder, url
            });
        }catch (Exception e){
            println "exception"
            e.printStackTrace()
        }
        // finally {
        //     // thread_pool.shutdown()
        //     // println "submit finish!"
        // }
		// new File(folder, "${url.tokenize('/')[-1]}").withOutputStream { out ->
		//   out << new URL(url).openStream()
		// }
	}
	
    /**
     * Main.
     */
    static main(args)
    {    
        println args
        if (args)
        {
            
            String url = args[0]
            new ILoveCk101().run(url)

            println imageQueue.size()
            
            try {
                for (job in imageQueue) {
                    thread_pool.submit({
                        downloadImageJob(job["folder"], job["url"])
                    });
                }

                // thread_pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                thread_pool.shutdown()
            }catch (Exception e){
                println "exception"
                e.printStackTrace()
            }finally {
                // thread_pool.shutdown()
                println "submit finish!"
            }
            
            try {
              //thread_pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
              // println thread_pool.awaitTermination(10 * 1000, TimeUnit.NANOSECONDS);
              println thread_pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
                
              println "thread pool is close"
              
            } catch (InterruptedException e) {
              println e              
            }
        }
        else
        {
            println "Please provide URL from ck101"
        }
    }
}
