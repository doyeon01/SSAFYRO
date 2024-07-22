import { useState } from 'react';
import Essay from './components/Essay';
import Test from './components/Test';

export default function First() {
  const [activeTab, setActiveTab] = useState('essay');

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  return (
    <>
      <div className="mb-4 border-b border-gray-200 dark:border-gray-700">
        <ul className="flex flex-wrap -mb-px text-sm font-medium text-center" id="default-tab" data-tabs-toggle="#default-tab-content" role="tablist">
          <li className="me-2" role="presentation">
            <button
              className={`inline-block p-4 border-b-2 rounded-t-lg ${activeTab === 'essay' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300'}`}
              id="essay-tab"
              data-tabs-target="#essay"
              type="button"
              role="tab"
              aria-controls="profile"
              aria-selected={activeTab === 'essay'}
              onClick={() => handleTabClick('essay')}
            >
              에세이
            </button>
          </li>
          <li className="me-2" role="presentation">
            <button
              className={`inline-block p-4 border-b-2 rounded-t-lg ${activeTab === 'sw' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300'}`}
              id="sw-tab"
              data-tabs-target="#sw"
              type="button"
              role="tab"
              aria-controls="sw"
              aria-selected={activeTab === 'sw'}
              onClick={() => handleTabClick('sw')}
            >
              SW 적성진단
            </button>
          </li>
        </ul>
      </div>
      <div id="default-tab-content">
        {activeTab === 'essay' && (
          <div className="p-4 rounded-lg bg-gray-50 dark:bg-gray-800" id="essay" role="tabpanel" aria-labelledby="essay-tab">
            <Essay />
          </div>
        )}
        {activeTab === 'sw' && (
          <div className="p-4 rounded-lg bg-gray-50 dark:bg-gray-800" id="sw" role="tabpanel" aria-labelledby="sw-tab">
            <Test />
          </div>
        )}
      </div>
    </>
  );
}
