import React, { useState } from 'react';
import { Search, MapPin, Compass, ArrowRight, Loader2, Sparkles } from 'lucide-react';
import { GlobalMouzaSearchResult } from '../types';

interface QuickMouzaFinderProps {
  onSelectMouza: (item: GlobalMouzaSearchResult) => void;
  lang: 'en' | 'bn';
}

export const QuickMouzaFinder: React.FC<QuickMouzaFinderProps> = ({ onSelectMouza, lang }) => {
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [results, setResults] = useState<GlobalMouzaSearchResult[]>([]);
  const [isSearching, setIsSearching] = useState<boolean>(false);
  const [hasSearched, setHasSearched] = useState<boolean>(false);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchTerm.trim() || searchTerm.trim().length < 2) return;

    try {
      setIsSearching(true);
      setHasSearched(true);
      const res = await fetch(`/api/locations/search?q=${encodeURIComponent(searchTerm.trim())}`);
      const json = await res.json();
      if (json.success && Array.isArray(json.data)) {
        setResults(json.data);
      } else {
        setResults([]);
      }
    } catch {
      setResults([]);
    } finally {
      setIsSearching(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-slate-200 p-6 md:p-8 space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between pb-6 border-b border-slate-100 gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
            <Compass className="w-5 h-5 text-emerald-600" />
            <span>{lang === 'bn' ? 'সমগ্র পশ্চিমবঙ্গ মৌজা অনুসন্ধান ডিরেক্টরি' : 'All-Bengal Mouza & JL Directory'}</span>
          </h2>
          <p className="text-sm text-slate-500 mt-1">
            {lang === 'bn'
              ? 'মৌজার নাম (ইংরেজি/বাংলা) অথবা JL নম্বর দিয়ে সরাসরি অনুসন্ধান করে এক ক্লিকে ফর্ম পূরণ করুন'
              : 'Search directly across 40,000+ mouzas by English/Bengali name or JL number'}
          </p>
        </div>
      </div>

      <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <input
            type="text"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
            placeholder={lang === 'bn' ? 'মৌজার নাম লিখুন (উদাঃ Gouripur, শ্যামপুর, বা 055)...' : 'Type mouza name (e.g. Gouripur, Shyampur, or 055)...'}
            className="w-full bg-slate-50 border border-slate-300 hover:border-slate-400 focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/10 rounded-xl px-4 py-3.5 text-sm font-semibold text-slate-900 placeholder:text-slate-400 transition-colors"
          />
        </div>

        <button
          type="submit"
          disabled={isSearching}
          className="px-6 py-3.5 bg-emerald-700 hover:bg-emerald-800 text-white font-bold rounded-xl shadow-md transition-all flex items-center justify-center gap-2 disabled:opacity-60"
        >
          {isSearching ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
          <span>{lang === 'bn' ? 'মৌজা খুঁজুন' : 'Find Mouza'}</span>
        </button>
      </form>

      {hasSearched && (
        <div className="space-y-3 pt-2">
          <div className="flex items-center justify-between text-xs font-semibold text-slate-500">
            <span>{lang === 'bn' ? `পাওয়া ফলাফল: ${results.length} টি মৌজা` : `Found ${results.length} matching mouzas`}</span>
            <span>{lang === 'bn' ? 'ক্লিক করে সরাসরি সিলেক্ট করুন' : 'Click to select directly'}</span>
          </div>

          {results.length === 0 && !isSearching && (
            <div className="text-center py-12 bg-slate-50 rounded-xl border border-dashed border-slate-200">
              <MapPin className="w-8 h-8 text-slate-300 mx-auto mb-2" />
              <p className="text-sm font-semibold text-slate-700">
                {lang === 'bn' ? 'কোনো মৌজা পাওয়া যায়নি।' : 'No matching mouzas found.'}
              </p>
              <p className="text-xs text-slate-400 mt-1">
                {lang === 'bn' ? 'বানান যাচাই করে আবার চেষ্টা করুন।' : 'Please check the spelling or enter a JL number.'}
              </p>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3 max-h-[460px] overflow-y-auto pr-1">
            {results.map((r, i) => (
              <div
                key={`${r.districtCode}-${r.blockCode}-${r.mouzaCode}-${i}`}
                onClick={() => onSelectMouza(r)}
                className="group p-4 bg-slate-50 hover:bg-emerald-50/70 border border-slate-200 hover:border-emerald-300 rounded-xl cursor-pointer transition-all flex items-center justify-between gap-3 shadow-sm hover:shadow"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-slate-900 group-hover:text-emerald-900 text-sm">
                      {r.mouzaNameEn}
                    </span>
                    {r.mouzaNameBn && (
                      <span className="text-xs text-slate-500 font-medium">
                        ({r.mouzaNameBn})
                      </span>
                    )}
                  </div>
                  <div className="text-xs text-slate-500 mt-1 flex items-center gap-1.5 flex-wrap">
                    <span className="bg-slate-200/80 text-slate-700 px-1.5 py-0.5 rounded text-[10px] font-mono font-bold">
                      JL {r.mouzaCode}
                    </span>
                    <span>&bull;</span>
                    <span>{r.blockName}</span>
                    <span>&bull;</span>
                    <span className="font-medium text-slate-700">{r.districtName}</span>
                  </div>
                </div>

                <div className="w-8 h-8 rounded-full bg-white group-hover:bg-emerald-600 group-hover:text-white flex items-center justify-center text-slate-400 transition-colors shrink-0 shadow-sm">
                  <ArrowRight className="w-4 h-4" />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {!hasSearched && (
        <div className="p-6 bg-slate-50/70 rounded-xl border border-slate-200/60 text-xs text-slate-500 space-y-3">
          <div className="flex items-center gap-2 font-bold text-slate-700">
            <Sparkles className="w-4 h-4 text-emerald-600" />
            <span>{lang === 'bn' ? 'দ্রুত মৌজা সন্ধানের টিপস:' : 'Quick Search Tips:'}</span>
          </div>
          <ul className="list-disc pl-5 space-y-1.5 text-slate-600">
            <li>{lang === 'bn' ? 'আপনি বাংলা বর্ণমালা দিয়েও মৌজার নাম খুঁজতে পারেন (উদাঃ শ্যামপুর, হরিপুর)' : 'Search using either English or Bengali characters (e.g. Haripur, হরিপুর)'}</li>
            <li>{lang === 'bn' ? 'JL নম্বর (Jurisdiction List Number) সরাসরি প্রবেশ করাতে পারেন' : 'Enter specific 3-digit JL numbers like 055 or 012 directly'}</li>
            <li>{lang === 'bn' ? 'ফলাফলে ক্লিক করলেই সার্চ ফর্ম স্বয়ংক্রিয়ভাবে জেলা, ব্লক ও মৌজা লোড করে নেবে' : 'Clicking any result will automatically populate District, Block, and Mouza in the search tab'}</li>
          </ul>
        </div>
      )}
    </div>
  );
};
