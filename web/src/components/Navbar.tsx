import React from 'react';
import { Search, Compass, Calculator, Bookmark, FileText, Globe } from 'lucide-react';

interface NavbarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  lang: 'en' | 'bn';
  setLang: (lang: 'en' | 'bn') => void;
  savedCount: number;
}

export const Navbar: React.FC<NavbarProps> = ({
  activeTab,
  setActiveTab,
  lang,
  setLang,
  savedCount
}) => {
  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-3 cursor-pointer" onClick={() => setActiveTab('search')}>
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-700 to-emerald-500 flex items-center justify-center text-white shadow-md shadow-emerald-500/20">
              <span className="font-extrabold text-lg">ভূমি</span>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="font-bold text-slate-900 tracking-tight text-lg">BanglarBhumi</span>
                <span className="bg-emerald-100 text-emerald-800 text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider">Pro</span>
              </div>
              <p className="text-xs text-slate-500 font-medium">
                {lang === 'bn' ? 'পশ্চিমবঙ্গ ভূমি ও সম্পত্তি রেকর্ড পোর্টাল' : 'West Bengal Land & Property Records'}
              </p>
            </div>
          </div>

          <nav className="hidden md:flex items-center gap-1">
            <button
              onClick={() => setActiveTab('search')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold transition-colors ${
                activeTab === 'search'
                  ? 'bg-emerald-50 text-emerald-700'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              <Search className="w-4 h-4" />
              <span>{lang === 'bn' ? 'রেকর্ড অনুসন্ধান' : 'Search Record'}</span>
            </button>

            <button
              onClick={() => setActiveTab('mouza-finder')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold transition-colors ${
                activeTab === 'mouza-finder'
                  ? 'bg-emerald-50 text-emerald-700'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              <Compass className="w-4 h-4" />
              <span>{lang === 'bn' ? 'মৌজা সন্ধান' : 'Mouza Directory'}</span>
            </button>

            <button
              onClick={() => setActiveTab('calculator')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold transition-colors ${
                activeTab === 'calculator'
                  ? 'bg-emerald-50 text-emerald-700'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              <Calculator className="w-4 h-4" />
              <span>{lang === 'bn' ? 'জমি পরিমাপক' : 'Land Converter'}</span>
            </button>

            <button
              onClick={() => setActiveTab('saved')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold transition-colors relative ${
                activeTab === 'saved'
                  ? 'bg-emerald-50 text-emerald-700'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              <Bookmark className="w-4 h-4" />
              <span>{lang === 'bn' ? 'সংরক্ষিত' : 'Saved'}</span>
              {savedCount > 0 && (
                <span className="w-5 h-5 bg-emerald-600 text-white rounded-full text-[10px] font-bold flex items-center justify-center">
                  {savedCount}
                </span>
              )}
            </button>

            <button
              onClick={() => setActiveTab('guide')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold transition-colors ${
                activeTab === 'guide'
                  ? 'bg-emerald-50 text-emerald-700'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              <FileText className="w-4 h-4" />
              <span>{lang === 'bn' ? 'মিউটেশন নির্দেশিকা' : 'Citizen Guide'}</span>
            </button>
          </nav>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setLang(lang === 'en' ? 'bn' : 'en')}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
            >
              <Globe className="w-3.5 h-3.5 text-emerald-600" />
              <span>{lang === 'en' ? 'বাংলা (BN)' : 'English (EN)'}</span>
            </button>
          </div>
        </div>
      </div>

      <div className="md:hidden flex items-center justify-around border-t border-slate-100 py-2 bg-slate-50 px-2 overflow-x-auto">
        <button
          onClick={() => setActiveTab('search')}
          className={`flex flex-col items-center gap-1 p-1.5 rounded-lg text-xs font-medium ${
            activeTab === 'search' ? 'text-emerald-700 font-bold' : 'text-slate-500'
          }`}
        >
          <Search className="w-4 h-4" />
          <span>{lang === 'bn' ? 'অনুসন্ধান' : 'Search'}</span>
        </button>

        <button
          onClick={() => setActiveTab('mouza-finder')}
          className={`flex flex-col items-center gap-1 p-1.5 rounded-lg text-xs font-medium ${
            activeTab === 'mouza-finder' ? 'text-emerald-700 font-bold' : 'text-slate-500'
          }`}
        >
          <Compass className="w-4 h-4" />
          <span>{lang === 'bn' ? 'মৌজা' : 'Mouza'}</span>
        </button>

        <button
          onClick={() => setActiveTab('calculator')}
          className={`flex flex-col items-center gap-1 p-1.5 rounded-lg text-xs font-medium ${
            activeTab === 'calculator' ? 'text-emerald-700 font-bold' : 'text-slate-500'
          }`}
        >
          <Calculator className="w-4 h-4" />
          <span>{lang === 'bn' ? 'ক্যালকুলেটর' : 'Calculator'}</span>
        </button>

        <button
          onClick={() => setActiveTab('saved')}
          className={`flex flex-col items-center gap-1 p-1.5 rounded-lg text-xs font-medium relative ${
            activeTab === 'saved' ? 'text-emerald-700 font-bold' : 'text-slate-500'
          }`}
        >
          <Bookmark className="w-4 h-4" />
          <span>{lang === 'bn' ? 'সংরক্ষিত' : 'Saved'}</span>
          {savedCount > 0 && (
            <span className="absolute -top-1 -right-1 w-4 h-4 bg-emerald-600 text-white rounded-full text-[9px] flex items-center justify-center font-bold">
              {savedCount}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveTab('guide')}
          className={`flex flex-col items-center gap-1 p-1.5 rounded-lg text-xs font-medium ${
            activeTab === 'guide' ? 'text-emerald-700 font-bold' : 'text-slate-500'
          }`}
        >
          <FileText className="w-4 h-4" />
          <span>{lang === 'bn' ? 'নির্দেশিকা' : 'Guide'}</span>
        </button>
      </div>
    </header>
  );
};
