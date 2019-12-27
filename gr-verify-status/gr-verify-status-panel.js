// Copyright (C) 2019 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
(function() {
  'use strict';
  const Defs = {};
  /**
   * @typedef {{
   *   summary: Object,
   *   results: Array,
   * }}
   */
  Defs.verifyStatus;
    /**
   * @typedef {{
   *   options: Object,
   * }}
   */
  Defs.verifyStatusConfig;

  Polymer({
    is: 'gr-verify-status-panel',

    properties: {
        verifyStatus: {
            /** @type {Defs.verifyStatus} */
            type: Object,
        },
        verifyStatusConfig: {
            /** @type {Defs.verifyStatusConfig} */
            type: Object,
        },
        revision: {
            type: Object,
        },
        change: {
            type: Object,
        }
    },

    attached() {
        this._fetchData(this.revision);
    },

    _fetchData(revision) {
      if (!revision) return;
      const configendpoint = '/config/server/verify-status~config';

      const errFn = response => {
          this.fire('page-error', {response});
      };

      this.plugin.restApi().get(configendpoint, errFn).then(r => {
          let options =r;
          this.verifyStatusConfig = {options};
          const query ='/verify-status~verifications?sort='+
                        options.sort_jobs_panel+'&filter=CURRENT';
          const endpoint = '/changes/' + this.change.id + '/revisions/' +
                       revision._number + query;
          this.plugin.restApi().get(endpoint, errFn).then(r => {
              let summary = {failed:0, passed:0, warning:0, inprogress:0};
              let results = [];
              for (let checkid in r) {
                  let check= r[checkid];
                  if (check.value == '2') {
                      if (options.enable_in_progress_status) {
                          check.status_icon = "M6 2v6h.01L6 8.01 10 12l-4 4 .01.01H6V22h12v-5.99h-.01L18 16l-4-4 4-3.99-.01-.01H18V2H6z";
                          check.color = "rgb(255, 166, 47)";
                          summary.inprogress += 1;
                      }
                      else {
                          check.status_icon = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z";
                          check.color = "rgb(56, 142, 60)";
                          summary.passed += 1;
                      }
                  }
                  else if (check.value == '1') {
                      summary.passed += 1;
                      check.status_icon = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z";
                      check.color = "rgb(56, 142, 60)";
                  }
                  else if (check.value == '-1') {
                      summary.failed += 1;
                      check.status_icon = "M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z";
                      check.color = "rgb(211, 47, 47)";
                  }
                  else if (check.value == '0') {
                      summary.warning += 1;
                      check.status_icon = "M11,9.5 C11,9.225 11.225,9 11.5,9 L12.5,9 C12.775,9 13,9.225 13,9.5 L13,16.5 C13,16.775 12.775,17 12.5,17 L11.5,17 C11.225,17 11,16.775 11,16.5 L11,9.5 Z M11,19.5 C11,19.225 11.225,19 11.5,19 L12.5,19 C12.775,19 13,19.225 13,19.5 L13,20.5 C13,20.775 12.775,21 12.5,21 L11.5,21 C11.225,21 11,20.775 11,20.5 L11,19.5 Z M23.947,23.277 L12.447,0.276 C12.263,-0.092 11.737,-0.092 11.553,0.276 L0.053,23.277 C-0.113,23.609 0.129,24 0.5,24 L23.5,24 C23.871,24 24.113,23.609 23.947,23.277 L23.947,23.277 Z";
                      check.color = "rgb(255, 166, 47)";
                  }
                  if (check.abstain == true) {
                      check.non_voting_icon="M9,16 C7.43,16 5.985,15.474 4.816,14.598 L14.598,4.816 C15.474,5.985 16,7.43 16,9 C16,12.86 12.86,16 9,16 M9,2 C10.57,2 12.015,2.526 13.184,3.402 L3.402,13.184 C2.526,12.015 2,10.57 2,9 C2,5.14 5.14,2 9,2 M9,0 C4.029,0 0,4.029 0,9 C0,13.971 4.029,18 9,18 C13.971,18 18,13.971 18,9 C18,4.029 13.971,0 9,0";
                  }
                  results.push(check);
             };

             this.verifyStatus = {summary, results};
         });
      });
    },
  });
}());
