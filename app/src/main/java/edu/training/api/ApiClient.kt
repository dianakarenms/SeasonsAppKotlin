package edu.training.seasonsappkotlin

import android.content.Context
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.HurlStack
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import edu.training.utils.Repository.hideProgressDialog
import edu.training.utils.Repository.showProgressDialog
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

/**
 * Created by dianakarenms on 5/28/17.
 */

class ApiClient

private constructor() {
    private var mRequestQueue: RequestQueue? = null

    // Don't forget to start the volley request queue
    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                val cache = DiskBasedCache(mCtx!!.cacheDir, 10 * 1024 * 1024)
                val network = BasicNetwork(HurlStack())
                mRequestQueue = RequestQueue(cache, network)
                mRequestQueue!!.start()
            }
            return mRequestQueue!!
        }

    init {
        mRequestQueue = requestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.tag = tag
        requestQueue.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = "TAG"
        requestQueue.add(req)
    }

    fun cancelPendingRequest(tag: String) {
        if (mRequestQueue != null) mRequestQueue!!.cancelAll(tag)
    }

    private fun cancelAllRequest() {
        mRequestQueue!!.cancelAll { true }
    }

    //---------- REQUEST -------------------------------------------------------------------------->
    class GsonRequest<T>
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param method  Method.GET or Method.POST
     * @param headers Map of request headers
     * @param params  Map of request parameters
     */
    (url: String, private val clazz: Class<T>, method: Int, private val headers: Map<String, String>?, private val params: Map<String, String>?,
     private val listener: Response.Listener<T>, errorListener: Response.ErrorListener, private val showProgress: Boolean) : Request<T>(method, url, errorListener) {
        private val gson = Gson()
        private val activityName: String

        init {
            this.activityName = mCtx!!.javaClass.simpleName

            retryPolicy = DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            /*switch (headers) {
                case HEADER_DEFAULT:
                    this.headers = new LinkedHashMap<>();
                    this.headers.put("Content-Type", "application/json");
                    this.headers.put("trakt-api-key", ApiConfig.apiKeyTrakt); //mCtx.getResources().getString(R.string.trakt_api)
                    this.headers.put("trakt-api-version", "2");
                    break;
                default:
                    this.headers = new LinkedHashMap<>();
            }*/

            if (showProgress) {
                showProgressDialog(mCtx)
            }
        }

        @Throws(AuthFailureError::class)
        override fun getHeaders(): Map<String, String> {
            return headers ?: super.getHeaders()
        }

        @Throws(AuthFailureError::class)
        public override fun getParams(): Map<String, String> {
            return params ?: super.getParams()
        }

        override fun deliverResponse(response: T) {
            listener.onResponse(response)
        }

        override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
            if (showProgress) {
                hideProgressDialog()
            }
            try {
                val json = String(
                        response?.data ?: ByteArray(0),
                        Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

                val result = gson.fromJson(json, clazz)
                val entry = HttpHeaderParser.parseCacheHeaders(response)
                Log.d(activityName, "Response for: $url[ $json ] ")

                return Response.success(result, entry)

            } catch (e: UnsupportedEncodingException) {
                return Response.error(VolleyError("Error parsing response"))
            } catch (e: JsonSyntaxException) {
                return Response.error(VolleyError("Error parsing response"))
            }

        }

        override fun parseNetworkError(volleyError: VolleyError): VolleyError {
            var error = volleyError
            if (showProgress)
                hideProgressDialog()

            Log.e(activityName, "Error for " + url + ": " + error.toString())

            val message: String

            // TmdbShow a default error message depending on error type
            if (error is NetworkError) {
                message = mCtx!!.getString(R.string.error_network)
            } else if (error is ServerError) {
                message = mCtx!!.getString(R.string.error_server)
            } else if (error is AuthFailureError) {
                message = mCtx!!.getString(R.string.error_auth_failure)
            } else if (error is ParseError) {
                message = mCtx!!.getString(R.string.error_parse)
            } else if (error is NoConnectionError) {
                message = mCtx!!.getString(R.string.error_no_connection)
            } else if (error is TimeoutError) {
                message = mCtx!!.getString(R.string.error_time_out)
            } else
                message = ""

            // Check if server returned data
            if (error.networkResponse != null && error.networkResponse.data != null) {
                // TODO: Handle this case
                Log.e("ApiClient:", "Network Response Data is null")
                /* ApiResponse errorResponse = null;
                String json = new String(error.networkResponse.data);
                if (error instanceof AuthFailureError){
                    if(!(mCtx instanceof LoginActivity)) {
                        sSession.logoutUser();
                        message = "Por favor inicie sesión.";
                    }
                } else {
                    errorResponse = gson.fromJson(json, ApiResponse.class);
                }
                try {
                    // Get error message to show to user if its available
                    if(errorResponse != null) {
                        if(errorResponse.getError() != null) {
                            message = errorResponse.getError().getUser();
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    message = mCtx.getString(R.string.error_server);
                }*/
            }

            error = VolleyError(message)

            return super.parseNetworkError(error)
        }
    }

    companion object {
        private var mInstance: ApiClient? = null
        private var mCtx: Context? = null

        @Synchronized
        fun getInstance(context: Context): ApiClient {
            mCtx = context
            if (mInstance == null) {
                mInstance = ApiClient()
            }
            return mInstance as ApiClient
        }
    }
}
