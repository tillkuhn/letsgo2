import 'core-js/proposals/reflect-metadata';
import 'zone.js/dist/zone';
require('../manifest.webapp');

// https://github.com/Wykks/ngx-mapbox-gl
// Add this in your polyfill.ts file (https://github.com/Wykks/ngx-mapbox-gl/issues/136#issuecomment-496224634):

(window as any).global = window;
