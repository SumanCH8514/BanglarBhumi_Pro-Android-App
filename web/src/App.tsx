import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { SearchForm } from './components/SearchForm';
import { ResultView } from './components/ResultView';
import { QuickMouzaFinder } from './components/QuickMouzaFinder';
import { LandCalculator } from './components/LandCalculator';
import { SavedRecords } from './components/SavedRecords';
import { FeeGuide } from './components/FeeGuide';
import { LandRecordResult, SavedRecordItem, GlobalMouzaSearchResult } from './types';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<string>('search');
  const [lang, setLang] = useState<'en' | 'bn'>('bn');
  const [currentResult, setCurrentResult] = useState<LandRecordResult | null>(null);
  const [savedList, setSavedList] = useState<SavedRecordItem[]>([]);
  const [presetSelection, setPresetSelection] = useState<{
    districtCode: string;
    districtName: string;
    blockCode: string;
    blockName: string;
    mouzaCode: string;
    mouzaName: string;
  } | null>(null);

  useEffect(() => {
    try {
      const stored = localStorage.getItem('banglarbhumi_saved_records');
      if (stored) {
        setSavedList(JSON.parse(stored));
      }
    } catch {}
  }, []);

  const handleSaveRecord = (record: LandRecordResult) => {
    const existingIndex = savedList.findIndex(
      item =>
        item.metadata.districtCode === record.metadata.districtCode &&
        item.metadata.blockCode === record.metadata.blockCode &&
        item.metadata.mouzaCode === record.metadata.mouzaCode &&
        item.metadata.searchType === record.metadata.searchType &&
        item.metadata.searchNumber === record.metadata.searchNumber
    );

    let updated: SavedRecordItem[];
    if (existingIndex >= 0) {
      updated = savedList.filter((_, idx) => idx !== existingIndex);
    } else {
      const newItem: SavedRecordItem = {
        id: `${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
        savedAt: new Date().toISOString(),
        metadata: record.metadata,
        html: record.html
      };
      updated = [newItem, ...savedList];
    }

    setSavedList(updated);
    try {
      localStorage.setItem('banglarbhumi_saved_records', JSON.stringify(updated));
    } catch {}
  };

  const handleDeleteRecord = (id: string) => {
    const updated = savedList.filter(item => item.id !== id);
    setSavedList(updated);
    try {
      localStorage.setItem('banglarbhumi_saved_records', JSON.stringify(updated));
    } catch {}
  };

  const isCurrentRecordSaved = Boolean(
    currentResult &&
      savedList.some(
        item =>
          item.metadata.districtCode === currentResult.metadata.districtCode &&
          item.metadata.blockCode === currentResult.metadata.blockCode &&
          item.metadata.mouzaCode === currentResult.metadata.mouzaCode &&
          item.metadata.searchType === currentResult.metadata.searchType &&
          item.metadata.searchNumber === currentResult.metadata.searchNumber
      )
  );

  const handleSelectMouzaFromFinder = (item: GlobalMouzaSearchResult) => {
    setPresetSelection({
      districtCode: item.districtCode,
      districtName: item.districtName,
      blockCode: item.blockCode,
      blockName: item.blockName,
      mouzaCode: item.mouzaCode,
      mouzaName: item.mouzaNameEn
    });
    setCurrentResult(null);
    setActiveTab('search');
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col selection:bg-emerald-500 selection:text-white">
      <Navbar
        activeTab={activeTab}
        setActiveTab={tab => {
          setActiveTab(tab);
          if (tab === 'search') {
            setCurrentResult(null);
          }
        }}
        lang={lang}
        setLang={setLang}
        savedCount={savedList.length}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeTab === 'search' && (
          <>
            {currentResult ? (
              <ResultView
                result={currentResult}
                onBack={() => setCurrentResult(null)}
                onSave={handleSaveRecord}
                isSaved={isCurrentRecordSaved}
                lang={lang}
              />
            ) : (
              <SearchForm
                onSearchResult={res => setCurrentResult(res)}
                lang={lang}
                presetSelection={presetSelection}
              />
            )}
          </>
        )}

        {activeTab === 'mouza-finder' && (
          <QuickMouzaFinder
            onSelectMouza={handleSelectMouzaFromFinder}
            lang={lang}
          />
        )}

        {activeTab === 'calculator' && (
          <LandCalculator lang={lang} />
        )}

        {activeTab === 'saved' && (
          <SavedRecords
            savedList={savedList}
            onOpenRecord={rec => {
              setCurrentResult(rec);
              setActiveTab('search');
            }}
            onDeleteRecord={handleDeleteRecord}
            lang={lang}
          />
        )}

        {activeTab === 'guide' && (
          <FeeGuide lang={lang} />
        )}
      </main>

      <footer className="bg-white border-t border-slate-200 py-6 text-xs text-slate-500 print:hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <span className="font-bold text-slate-800">BanglarBhumi-Pro</span>
            <span>&bull;</span>
            <span>Production Grade Land & Property Record System</span>
          </div>
          <div className="flex items-center gap-4 text-slate-400">
            <span>Data synced with Banglarbhumi RoR standards</span>
            <span>&bull;</span>
            <span>Version 1.0.0</span>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default App;
