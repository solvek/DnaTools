// ==UserScript==
// @name         MHFetchGraph
// @namespace    http://tampermonkey.net/
// @version      2025-04-28
// @description  try to take over the world!
// @author       You
// @match        http://*/*
// @icon         data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==
// @grant        GM_xmlhttpRequest
// ==/UserScript==

(function() {
    'use strict';

    const PAGE_SIZE = 100;

    const mhFetchState = loadData();

    const originalOpen = XMLHttpRequest.prototype.open;
    const originalSend = XMLHttpRequest.prototype.send;

    XMLHttpRequest.prototype.open = function(method, url, async, user, password) {
        this._url = url; // Save URL to XHR object
        return originalOpen.apply(this, arguments);
    };


    XMLHttpRequest.prototype.send = function(body) {
        this.addEventListener('load', function() {
            if (this._url.includes("familygraphql.myheritage.com/fetch_dna_kit_ethnicity_estimate_overview/")) {
                mhFetchState.kitId = JSON.parse(this.responseText).data.dna_kit.id.substring(7)
                console.log('Current kit id: ', mhFetchState.kitId);
            }

            if (this._url.includes("familygraphql.myheritage.com/fetch_dna_matches_features_for_kit/")) {
                mhFetchState.matchesCount = JSON.parse(this.responseText).data.dna_kit.dna_matches.count;
            }

            if (this._url.includes("familygraphql.myheritage.com/fetch_dna_matches_for_kit/")) {
                if (mhFetchState.currentPage != null){
                    mhFetchState.matches = [];

                    const raw = JSON.parse(this.responseText).data.dna_kit.dna_matches.data;

                    for (let m of raw) {
                        try {
                            const indiv = m.other_dna_kit.associated_individual || m.other_dna_kit.member;
                            const match = {
                                id: m.id,
                                link: m.link,
                                name: indiv.name,
                                gender: indiv.gender,
                                age_group_in_years: indiv.age_group_in_years,
                                total_cm: m.total_shared_segments_length_in_cm,
                                largest_cm: m.largest_shared_segment_length_in_cm,
                                segments: m.total_shared_segments,
                                percentage: m.percentage_of_shared_segments
                            };
                            mhFetchState.matches.push(match);
                        }
                        catch(e){
                            console.log("Failed to parse match", JSON.stringify(raw));
                        }
                    }

                    download(JSON.stringify(mhFetchState), "application/json", "matches-"+String(mhFetchState.currentPage).padStart(5, '0')+".json");

                    const nextPage = mhFetchState.currentPage + 1;
                    mhFetchState.matches = null;
                    if ((nextPage > totalPages(PAGE_SIZE, mhFetchState.matchesCount)) || (nextPage >=5)){
                        mhFetchState.currentPage = null;
                        storeData(mhFetchState);
                        console.log("Fetch finished");
                    }
                    else {
                        fetchMatchesPage(nextPage);
                    }
                }
            }
        });
        return originalSend.apply(this, arguments);
    };

    window.addEventListener('load', function() {
        showButton();
    });

    function showButton(){
        const btn = document.createElement('button');
        btn.innerText = "Fetch graph";
        btn.style.position = 'fixed';
        btn.style.top = '20px';
        btn.style.right = '20px';
        btn.style.zIndex = 10000;
        btn.style.padding = '10px 15px';
        btn.style.backgroundColor = '#007BFF';
        btn.style.color = 'white';
        btn.style.border = 'none';
        btn.style.borderRadius = '5px';
        btn.style.cursor = 'pointer';
        btn.style.fontSize = '14px';

        btn.addEventListener('click', function() {
            const bodyData = `------WebKitFormBoundary8iXPlzd7pa1joZDC\r\nContent-Disposition: form-data; name=\"bearer_token\"\r\n\r\n4.cbdd04f731661d487d728136bd8331a0.71244161.1745938963.1440.7345153..6g0ibucs.3b6fd8806cf689b511d71445fe2493f6b15bb0577dd16a09636395c5570f9078\r\n------WebKitFormBoundary8iXPlzd7pa1joZDC\r\nContent-Disposition: form-data; name=\"query\"\r\n\r\n\"{dna_kit(id:\\\"dnakit-FE4A3EF6-BE08-4258-B60A-3DBB86BAB039\\\",lang:\\\"UK\\\"){dna_matches(offset:10,limit:10,sort_query:\\\"total_shared_segments_length_in_cm\\\",query:\\\"\\\",filter:\\\"0\\\",filter_by_relationship:\\\"0\\\",filter_by_country:\\\"0\\\",filter_by_labels:\\\"\\\"){data{id link is_new complete_dna_relationships{relationship_type relationship_degree}refined_dna_relationships{relationship_type relationship_degree}dna_cm_explainer{relationships{...dna_match_relationship}most_probable_relationships{...dna_match_relationship}calculation_strategy}exact_dna_relationship genealogical_relationship total_shared_segments_length_in_cm largest_shared_segment_length_in_cm percentage_of_shared_segments total_shared_segments is_recently_recalculated confidence_level other_dna_kit{submitter{id name name_transliterated first_name first_name_transliterated link is_public}member{id first_name first_name_transliterated name name_transliterated gender age_group age_group_in_years personal_photo{...PERSONAL_PHOTO_INFO}country_code country is_public link}associated_individual{id first_name first_name_transliterated name name_transliterated gender age_group age_group_in_years personal_photo{...PERSONAL_PHOTO_INFO}birth_place tree{...tree_info}relationship{...RELATIONSHIP_INFO}link_in_pedigree_tree link_in_tree}}}}}}fragment dna_match_relationship on DnaMatchRelationship{relationship_type relationship_class path_type probability most_recent_common_ancestor_relationship_type most_recent_common_ancestor_relationship_class}fragment tree_info on Tree{id name link individual_count site{is_request_membership_allowed creator{id name name_transliterated country country_code link is_public}}}fragment PERSONAL_PHOTO_INFO on Photo{thumbnails(thumbnail_size:\\\"96x96\\\"){url}}fragment RELATIONSHIP_INFO on Relationship{relationship_description}\"\r\n------WebKitFormBoundary8iXPlzd7pa1joZDC\r\nContent-Disposition: form-data; name=\"operation\"\r\n\r\n\r\n------WebKitFormBoundary8iXPlzd7pa1joZDC\r\nContent-Disposition: form-data; name=\"variables\"\r\n\r\n\r\n------WebKitFormBoundary8iXPlzd7pa1joZDC\r\nContent-Disposition: form-data; name=\"site_id\"\r\n\r\n80443353\r\n------WebKitFormBoundary8iXPlzd7pa1joZDC\r\nContent-Disposition: form-data; name=\"description\"\r\n\r\nFETCH_DNA_MATCHES_FOR_KIT\r\n------WebKitFormBoundary8iXPlzd7pa1joZDC--\r\n`;

            GM_xmlhttpRequest({
                method: "POST",
                url: "https://familygraphql.myheritage.com/fetch_dna_matches_for_kit/",
                headers: {
                    "Content-Type": "multipart/form-data; boundary=----WebKitFormBoundary8iXPlzd7pa1joZDC",
                    "accept": "application/json, text/plain, */*",
                    "cache-control": "no-cache",
                    "pragma": "no-cache"
                },
                data: bodyData,
                onload: function (response) {
                    console.log("✅ Response received:", response.responseText);
                },
                onerror: function (err) {
                    console.error("❌ Request failed:", err);
                }
            });

            /*

            if (mhFetchState.currentPage != null){
                mhFetchState.currentPage = null;
                storeData(mhFetchState);
                return;
            }

            if (mhFetchState.kitId == null){
                alert("Current kit id not defined. Enter DNA section");
                return;
            }

            startFetching();
            */
        });

        document.body.appendChild(btn);
    }

    function startFetching(){
        fetchMatchesPage(1);
    }

    function fetchMatchesPage(pageNumber){
        mhFetchState.currentPage = pageNumber;
        storeData(mhFetchState);
        navigate("/dna/matches?kitId="+mhFetchState.kitId+"&ps="+PAGE_SIZE+"&p="+pageNumber)
    }

    function storeData(obj){
        const s = JSON.stringify(obj);
        localStorage.setItem("mhFetchState", s);
        return s;
    }

    function loadData(){
        return JSON.parse(localStorage.getItem('mhFetchState') || '{}');
    }

    function clearData(){
        localStorage.removeItem('mhFetchState');
    }

    function navigate(url){
        window.location.href = url;
    }

    function download(stringData, contentType, fileName){
        const blob = new Blob([stringData], { type: contentType });

        const link = document.createElement("a");
        if (link.download !== undefined) { // feature detection
            const url = URL.createObjectURL(blob);
            link.setAttribute("href", url);
            link.setAttribute("download", fileName);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    }

    function totalPages(pageSize, totalCount){
        return Math.ceil(totalCount/pageSize);
    }
})();