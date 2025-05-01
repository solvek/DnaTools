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

    const PAGE_SIZE = 20;
    const PAGE_SIZE_SHARED = 10; // DO NOT CHANGE THIS!

    var mhFetchState = loadData();

    var auth = null;
    var kitId = null;
    var started = false;

    var fetchShared = null;

    const originalOpen = XMLHttpRequest.prototype.open;
    const originalSend = XMLHttpRequest.prototype.send;

    XMLHttpRequest.prototype.open = function(method, url) {
        this._url = url;
        return originalOpen.apply(this, arguments);
    };

    XMLHttpRequest.prototype.send = function(body) {
         if (this._url.includes("familygraphql.myheritage.com/fetch_dna_kit_ethnicity_estimate_overview/")) {
             this.addEventListener('load', function(){
                 try {
                     kitId = JSON.parse(this.responseText).data.dna_kit.id.substring(7)
                     console.log('Current kit id: ', kitId);
                     startApp();
                 }
                 catch(e){
                     console.error("Failted to determine kitId", e);
                 }
             });
        }

        if (this._url.includes("familygraphql.myheritage.com/") && (body instanceof FormData)) {
            const token = body.get("bearer_token");
            if (token !== null) {
                console.log("🔐 Bearer token:", token);
                auth = token;
                startApp();
            }
        }

        return originalSend.apply(this, arguments);
    };

    function startApp(){
        if (started) return;

        if (!auth) return;

        if (mhFetchState == null){
            if (kitId == null) return;
            showButton();
        }
        else {
            startFetching();
        }

        started = true;
    }

    function startFetching(){
        if (mhFetchState.matchesCount == null){
            fetchMatchesCount();
            return;
        }

        fetchMatchesPage();
    }

    function fetchMatchesCount(){
        performRequest(
            "fetch_dna_matches_features_for_kit",
            '------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"bearer_token\"\r\n\r\n'+auth+'\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"query\"\r\n\r\n\"{dna_kit(id:\\\"dnakit-'+mhFetchState.kitId+'\\\",lang:\\\"UK\\\"){dna_labels{data{id name color_code}}dna_matches(offset:490,limit:10,sort_query:\\\"total_shared_segments_length_in_cm\\\",query:\\\"\\\",filter:\\\"0\\\",filter_by_relationship:\\\"0\\\",filter_by_country:\\\"0\\\",filter_by_labels:\\\"\\\"){count data{id is_favorite match_notes{data{id text}}match_labels{data{id}}ancestral_places{data{number_of_other_ancestors number_of_ancestors country_code state_code is_obfuscated}}theories_summary{id is_new number_of_theories best_theory_relationship{description}confirmation_status}common_ancestors{data{surname name{first_name last_name}other_name{first_name last_name}name_count other_name_count closest_relationship other_closest_relationship closest_relationship_side other_closest_relationship_side}}}}}}\"\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"operation\"\r\n\r\n\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"variables\"\r\n\r\n\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"site_id\"\r\n\r\n80443353\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"description\"\r\n\r\nFETCH_DNA_MATCHES_FEATURES_FOR_KIT\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"mhc#PHPSESSID\"\r\n\r\ne302870e8aba4902fee469cdb250d4b4\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3--\r\n',
            function(json){
                mhFetchState.matchesCount == json.data.dna_kit.dna_matches.count;
                storeData(mhFetchState);
                fetchMatchesPage();
            }
        );
    }

    function fetchMatchesPage(offset){
        if (offset){
            mhFetchState.matchesOffset = offset
            storeData(mhFetchState);
        }
        else {
            offset = mhFetchState.matchesOffset || 0;
        }

        console.log("Fetch page of matches, offset ", offset);
        performRequest(
            "fetch_dna_matches_for_kit",
            '------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"bearer_token\"\r\n\r\n'+auth+'\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"query\"\r\n\r\n\"{dna_kit(id:\\\"dnakit-'+mhFetchState.kitId+'\\\",lang:\\\"UK\\\"){dna_matches(offset:'+offset+',limit:'+PAGE_SIZE+',sort_query:\\\"total_shared_segments_length_in_cm\\\",query:\\\"\\\",filter:\\\"0\\\",filter_by_relationship:\\\"0\\\",filter_by_country:\\\"0\\\",filter_by_labels:\\\"\\\"){data{id link is_new complete_dna_relationships{relationship_type relationship_degree}refined_dna_relationships{relationship_type relationship_degree}dna_cm_explainer{relationships{...dna_match_relationship}most_probable_relationships{...dna_match_relationship}calculation_strategy}exact_dna_relationship genealogical_relationship total_shared_segments_length_in_cm largest_shared_segment_length_in_cm percentage_of_shared_segments total_shared_segments is_recently_recalculated confidence_level other_dna_kit{submitter{id name name_transliterated first_name first_name_transliterated link is_public}member{id first_name first_name_transliterated name name_transliterated gender age_group age_group_in_years personal_photo{...PERSONAL_PHOTO_INFO}country_code country is_public link}associated_individual{id first_name first_name_transliterated name name_transliterated gender age_group age_group_in_years personal_photo{...PERSONAL_PHOTO_INFO}birth_place tree{...tree_info}relationship{...RELATIONSHIP_INFO}link_in_pedigree_tree link_in_tree}}}}}}fragment dna_match_relationship on DnaMatchRelationship{relationship_type relationship_class path_type probability most_recent_common_ancestor_relationship_type most_recent_common_ancestor_relationship_class}fragment tree_info on Tree{id name link individual_count site{is_request_membership_allowed creator{id name name_transliterated country country_code link is_public}}}fragment PERSONAL_PHOTO_INFO on Photo{thumbnails(thumbnail_size:\\\"96x96\\\"){url}}fragment RELATIONSHIP_INFO on Relationship{relationship_description}\"\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"operation\"\r\n\r\n\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"variables\"\r\n\r\n\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"description\"\r\n\r\nFETCH_DNA_MATCHES_FOR_KIT\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3--\r\n',
            function(json){
                mhFetchState.matches = [];

                const raw = json.data.dna_kit.dna_matches.data;

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

                fetchShared = {matches: [...mhFetchState.matches]};
                fetchSharedPage();
            }
        );
    }

    function fetchSharedPage(){
        if (fetchShared.match){
            console.log("Fetch shared matches for ", fetchShared.match.id, ", offset ", fetchShared.matchOffset);
            performRequest(
                "dna_single_match_get_shared_matches",
                '------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"bearer_token\"\r\n\r\n'+auth+'\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"query\"\r\n\r\n\"{dna_match(id:\\\"'+fetchShared.match.id+'\\\",lang:\\\"UK\\\"){dna_shared_matches(offset:'+fetchShared.matchOffset+',limit:'+PAGE_SIZE_SHARED+',sort_shared_matches_by:\\\"match\\\"){count data{dna_matches_cluster_shared_segments_count is_obfuscated shared_member{personal_photo{...personal_photo_info}age_group gender name id}shared_individual{personal_photo{...personal_photo_info}age_group gender name id link_in_pedigree_tree tree{individual_count site{creator{id name country_code}}}}dna_match{...shared_match_details link dna_cm_explainer{most_probable_relationships{...dna_match_relationship}calculation_strategy}is_favorite match_notes{data{id text}}match_labels{data{id}}}other_dna_match{...shared_match_details dna_cm_explainer{most_probable_relationships{...dna_match_relationship}calculation_strategy}}shared_profile_personal_image{thumbnails(thumbnail_size:\\\"16x16\\\"){url}}}}}}fragment shared_match_details on DnaMatch{id refined_dna_relationships{relationship_type relationship_degree}exact_dna_relationship percentage_of_shared_segments total_shared_segments_length_in_cm}fragment personal_photo_info on Photo{thumbnails(thumbnail_size:\\\"96x96\\\"){url}}fragment dna_match_relationship on DnaMatchRelationship{relationship_type relationship_class path_type probability most_recent_common_ancestor_relationship_type most_recent_common_ancestor_relationship_class}\"\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"operation\"\r\n\r\n\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"variables\"\r\n\r\n\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"site_id\"\r\n\r\n80443353\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"description\"\r\n\r\nDNA Single Match - get shared matches\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"timeout\"\r\n\r\n[object Promise]\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3\r\nContent-Disposition: form-data; name=\"mhc#PHPSESSID\"\r\n\r\ne302870e8aba4902fee469cdb250d4b4\r\n------WebKitFormBoundaryHRCxw6zSWoDXD7V3--\r\n',
                function(json){
                    const shared_matches = json.data.dna_match.dna_shared_matches;
                    const total = shared_matches.count;
                    const raw = shared_matches.data;

                    if (raw != null){
                        for (let m of raw) {
                            try {
                                const other = m.other_dna_match;
                                fetchShared.match.shared.push({
                                    id: other.id,
                                    shared_cm: other.total_shared_segments_length_in_cm
                                });
                            }
                            catch(e){
                                console.error("Failed to parse shared match", JSON.stringify(m));
                            }
                        }
                    }

                    if (fetchShared.matchOffset < total && raw != null){
                        fetchShared.matchOffset += PAGE_SIZE_SHARED;
                    }
                    else {
                        fetchShared.match = null;
                    }
                    fetchSharedPage();
                }
            );
            return;
        }

        const currentMatch = fetchShared.matches.pop();
        if (currentMatch == null){
            var offset = mhFetchState.matchesOffset || 0;
            download(JSON.stringify(mhFetchState), "application/json", "matches-"+String(offset).padStart(6, '0')+".json");

            offset += PAGE_SIZE;

            if (offset < mhFetchState.matcheCount || offset < 50){
                mhFetchState.matches = null;
                fetchMatchesPage(offset);
            }
            else {
                mhFetchState = null;
                clearData();
            }

            return;
        }

        fetchShared.match = currentMatch;
        fetchShared.matchOffset = 0;
        currentMatch.shared = [];
        fetchSharedPage();
    }

    function performRequest(endpoint, body, onResult){
        GM_xmlhttpRequest({
            method: "POST",
            url: "https://familygraphql.myheritage.com/"+endpoint+"/",
            headers: {
                "Content-Type": "multipart/form-data; boundary=----WebKitFormBoundaryHRCxw6zSWoDXD7V3",
                "accept": "application/json, text/plain, */*",
                "cache-control": "no-cache",
                "pragma": "no-cache"
            },
            data: body,
            onload: function (response) {
                //console.log("✅ Response received:", response.responseText);
                try {
                    const j = JSON.parse(response.responseText);
                    onResult(j);
                }
                catch(e){
                    console.error("Cannot parse response as json", e, response);
                }
            },
            onerror: function (err) {
                console.error("❌ Request "+endpoint+" failed:", err);
            }
        });
    }

    function showButton(){
        const btn = document.createElement('button');
        btn.innerText = "Fetch graph";
        btn.style.position = 'fixed';
        btn.style.top = '60px';
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
            btn.remove();
            mhFetchState = {kitId: kitId};
            storeData(mhFetchState);
            startFetching();
        });

        document.body.appendChild(btn);
    }

    function storeData(obj){
        const s = JSON.stringify(obj);
        localStorage.setItem("mhFetchState", s);
        return s;
    }

    function loadData(){
        const j = localStorage.getItem('mhFetchState');
        return j ? JSON.parse(j) : null;
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