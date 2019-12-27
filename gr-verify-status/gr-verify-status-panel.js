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
              let summary = {failed:0, passed:0, notdone:0};
              let results = [];
              for (let checkid in r) {
                  let check= r[checkid];
                  if (check.value == '0') {
                     summary.notdone +=1;}
                  else if (check.value == 1) {
                      summary.passed +=1;
                  }
                  else {
                      summary.failed +=1;
                  }
                  results.push(check);
             };

             this.verifyStatus = {summary, results};
         });
      });
    },
  });
}());
